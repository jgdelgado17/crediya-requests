package co.com.crediya.requests.model.requests.gateways;

import co.com.crediya.requests.model.requests.Requests;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RequestsRepository {
    Mono<Requests> save(Requests requests);
    Mono<Requests> findById(Integer id);
    Flux<Requests> findByStatus(String status);
    Flux<Requests> findByTypeLoan(String typeLoan);
    Flux<Requests> findByEmail(String email);
}
