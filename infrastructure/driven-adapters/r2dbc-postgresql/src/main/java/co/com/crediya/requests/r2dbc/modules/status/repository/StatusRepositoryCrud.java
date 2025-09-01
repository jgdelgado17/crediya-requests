package co.com.crediya.requests.r2dbc.modules.status.repository;

import co.com.crediya.requests.r2dbc.modules.status.data.StatusEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface StatusRepositoryCrud extends ReactiveCrudRepository<StatusEntity, Integer>, ReactiveQueryByExampleExecutor<StatusEntity> {
    Mono<StatusEntity> findByNames(String names);
}
