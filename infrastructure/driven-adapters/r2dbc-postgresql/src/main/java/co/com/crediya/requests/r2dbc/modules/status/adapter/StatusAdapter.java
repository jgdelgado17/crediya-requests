package co.com.crediya.requests.r2dbc.modules.status.adapter;

import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.status.gateways.StatusRepository;
import co.com.crediya.requests.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.requests.r2dbc.modules.status.data.StatusEntity;
import co.com.crediya.requests.r2dbc.modules.status.repository.StatusRepositoryCrud;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StatusAdapter
    extends ReactiveAdapterOperations<Status, StatusEntity, Integer, StatusRepositoryCrud>
    implements StatusRepository {

    private static final Logger log = LoggerFactory.getLogger(StatusAdapter.class);

    protected StatusAdapter(StatusRepositoryCrud repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Status.class));
        this.repository = repository;
    }

    /**
     * Saves a status.
     *
     * <p>This method logs the status-saving process in the info level.
     *
     * @param status the status to be saved. The status cannot be null.
     * @return a {@link Mono} that emits the saved status or an error if the status names is already taken.
     */
    @Override
    public Mono<Status> save(Status status) {
        log.info("Saving status: {}", status.getNames());
        return super.save(status)
                .doOnSuccess(savedStatus -> log.info("Status saved successfully: {}", savedStatus.getNames()))
                .doOnError(error -> log.error("Error saving status: {}", error.getMessage()))
                .onErrorMap(error -> new RuntimeException(error.getMessage()));
    }

    /**
     * Finds a status by names.
     *
     * <p>This method logs the status-finding process in the info level.
     *
     * @param name the names of the status to be found. The names cannot be null or empty.
     * @return a {@link Mono} that emits the found status or an error if the status cannot be found.
     */
    @Override
    public Mono<Status> findByName(String name) {
        log.info("Finding status by names: {}", name);
        return repository.findByNames(name)
                .map(super::toEntity)
                .switchIfEmpty(Mono.fromRunnable(() -> log.warn("Status not found with names: {}", name)))
                .doOnSuccess(status -> log.info("Status found successfully: {}", name))
                .doOnError(error -> log.error("Error finding status: {}", error.getMessage()))
                .onErrorMap(error -> new RuntimeException(error.getMessage()));
    }
}
