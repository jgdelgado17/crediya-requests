package co.com.crediya.requests.consumer;

import co.com.crediya.requests.model.shared.exceptions.RecordNotFoundException;
import co.com.crediya.requests.model.user.User;
import co.com.crediya.requests.model.user.gateways.UserGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {
    private final WebClient client;
    private static final Logger log = LoggerFactory.getLogger(RestConsumer.class);

    @Value("${adapter.restconsumer.uri-user-by-email}")
    private String uriUser;

    @Value("${adapter.restconsumer.uri-users-by-list-emails}")
    private String uriUsers;

    @Override
    @CircuitBreaker(name = "validateEmailUser")
    public Mono<User> findUserByEmail(String email, String token) {
        log.info("Finding user by email: {}", email);
        return client
                .get()
                .uri(uriUser, email)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(User.class)
                .switchIfEmpty(Mono.error(new RecordNotFoundException("User not found with email: " + email)))
                .doOnSuccess(user -> log.info("User found successfully"))
                .doOnError(error -> log.error("Error finding user: {}", error.getMessage()));
    }

    @Override
    @CircuitBreaker(name = "validateEmailsUsers")
    public Flux<User> findUsersByEmails(List<String> emails, String token) {
        log.info("Finding users by emails: {}", emails);
        String emailsAsString = String.join(",", emails);

        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(uriUsers)
                        .queryParam("emails", emailsAsString)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(User.class)
                .doOnComplete(() -> log.info("Users found successfully"))
                .doOnError(error -> log.error("Error finding users: {}", error.getMessage()));
    }
}
