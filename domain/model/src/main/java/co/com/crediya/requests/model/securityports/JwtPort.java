package co.com.crediya.requests.model.securityports;

import reactor.core.publisher.Mono;

public interface JwtPort {
    Mono<Boolean> validateToken(String token);
}
