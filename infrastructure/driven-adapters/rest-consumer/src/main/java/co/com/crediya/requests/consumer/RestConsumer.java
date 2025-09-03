package co.com.crediya.requests.consumer;

import co.com.crediya.requests.model.user.User;
import co.com.crediya.requests.model.user.gateways.UserGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {
    private final WebClient client;

    @Override
    @CircuitBreaker(name = "validateEmailUser")
    public Mono<User> findUserByEmail(String email) {
        return client
                .get()
                .uri("/users/{email}", email)
                .retrieve()
                /*.onStatus(HttpStatus.NOT_FOUND::equals,
                        clientResponse -> Mono.error(new RuntimeException("Client error"))
                )*/
                .bodyToMono(User.class);
    }
}
