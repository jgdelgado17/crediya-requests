package co.com.crediya.requests.model.status.gateways;

import co.com.crediya.requests.model.status.Status;
import reactor.core.publisher.Mono;

public interface StatusRepository {
    Mono<Status> save(Status status);
    Mono<Status> findByName(String name);
}
