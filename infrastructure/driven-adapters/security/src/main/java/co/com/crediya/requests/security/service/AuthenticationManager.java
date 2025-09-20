package co.com.crediya.requests.security.service;

import co.com.crediya.requests.model.securityports.RoleEnum;
import co.com.crediya.requests.model.shared.enums.EnumUtils;
import co.com.crediya.requests.model.shared.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtAdapter jwtAdapter;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return jwtAdapter.validateToken(token)
                .filter(isValid -> isValid)
                .switchIfEmpty(Mono.error(new UnauthorizedException("Invalid token")))
                .flatMap(isValid -> Mono.fromCallable(() -> jwtAdapter.getAllClaimsFromToken(token))
                        .flatMap(claims -> {
                            String username = claims.getSubject();
                            String roleString = claims.get("role", String.class);

                            if (roleString == null || roleString.trim().isEmpty()) {
                                return Mono.error(new UnauthorizedException("Token contains empty role"));
                            }

                            return Mono.fromCallable(() -> EnumUtils.fromString(RoleEnum.class, roleString))
                                    .onErrorMap(e -> new UnauthorizedException("Token contains invalid role", e))
                                    .flatMap(validRole -> {
                                        List<SimpleGrantedAuthority> authorities = List.of(
                                                new SimpleGrantedAuthority("ROLE_" + validRole.getValue())
                                        );

                                        return Mono.just(new UsernamePasswordAuthenticationToken(
                                                username,
                                                token,
                                                authorities
                                        ));
                                    });
                        }));
    }
}
