package co.com.crediya.requests.model.user.gateways;

import co.com.crediya.requests.model.user.User;
import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<User> findUserByEmail(String email, String token);
}
