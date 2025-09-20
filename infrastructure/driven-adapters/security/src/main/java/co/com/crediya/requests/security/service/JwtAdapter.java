package co.com.crediya.requests.security.service;

import co.com.crediya.requests.model.securityports.JwtPort;
import co.com.crediya.requests.model.shared.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;

@Component
public class JwtAdapter implements JwtPort {
    @Value("${security.jwt.secret}")
    private String secret;

    private Key key;

    /**
     * Initialize the adapter, creating the HMAC key from the provided secret.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
                    Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token);
                    return true;
                })
                .onErrorMap(ExpiredJwtException.class, e -> new UnauthorizedException("Token expired", e))
                .onErrorMap(JwtException.class, e -> new UnauthorizedException("Invalid token", e))
                .onErrorMap(IllegalArgumentException.class, e -> new UnauthorizedException("Invalid token", e));
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
