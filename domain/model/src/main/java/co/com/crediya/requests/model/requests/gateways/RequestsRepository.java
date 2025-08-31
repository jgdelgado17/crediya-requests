package co.com.crediya.requests.model.requests.gateways;

import co.com.crediya.requests.model.requests.Requests;
import reactor.core.publisher.Mono;

public interface RequestsRepository {
    Mono<Requests> save(Requests requests);
    Mono<Requests> findByStatus(String status);
    Mono<Requests> findByTypeLoan(String typeLoan);
    Mono<Requests> findByEmail(String email);
}
