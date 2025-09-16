package co.com.crediya.requests.model.user.gateways;

import co.com.crediya.requests.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserGateway {
    Mono<User> findUserByEmail(String email, String token);
    Flux<User> findUsersByEmails(List<String> emails, String token);
}
