package co.com.crediya.requests.r2dbc.modules.requests.repository;

import co.com.crediya.requests.r2dbc.modules.requests.data.RequestsEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RequestsRepositoryCrud extends ReactiveCrudRepository <RequestsEntity, Integer>{
    Flux<RequestsEntity> findByStatus(String status);
    Flux<RequestsEntity> findByTypeLoan(String typeLoan);
    Flux<RequestsEntity> findByEmail(String email);
}
