package co.com.crediya.requests.consumer;

import co.com.crediya.requests.model.user.User;
import co.com.crediya.requests.model.user.gateways.UserGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {
    private final WebClient client;
    private static final Logger log = LoggerFactory.getLogger(RestConsumer.class);

    @Override
    @CircuitBreaker(name = "validateEmailUser")
    public Mono<User> findUserByEmail(String email) {
        log.info("Finding user by email: {}", email);
        return client
                .get()
                .uri("/users/{email}", email)
                .retrieve()
                .bodyToMono(User.class)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found with email: " + email)))
                .doOnSuccess(user -> log.info("User found successfully"))
                .doOnError(error -> log.error("Error finding user: {}", error.getMessage()));
    }
}
