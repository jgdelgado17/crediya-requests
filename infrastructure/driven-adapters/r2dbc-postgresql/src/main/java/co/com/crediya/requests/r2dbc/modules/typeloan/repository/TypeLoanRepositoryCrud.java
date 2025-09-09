package co.com.crediya.requests.r2dbc.modules.typeloan.repository;

import co.com.crediya.requests.r2dbc.modules.typeloan.data.TypeLoanEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TypeLoanRepositoryCrud extends ReactiveQueryByExampleExecutor<TypeLoanEntity>, ReactiveCrudRepository<TypeLoanEntity, Integer> {
    Mono<TypeLoanEntity> findByNames(String name);
}
