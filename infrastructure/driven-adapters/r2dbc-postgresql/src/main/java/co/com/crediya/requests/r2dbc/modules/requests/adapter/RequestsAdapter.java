package co.com.crediya.requests.r2dbc.modules.requests.adapter;

import co.com.crediya.requests.model.requests.Requests;
import co.com.crediya.requests.model.requests.gateways.RequestsRepository;
import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.r2dbc.modules.requests.data.RequestsEntity;
import co.com.crediya.requests.r2dbc.modules.requests.mapper.RequestsMapper;
import co.com.crediya.requests.r2dbc.modules.requests.repository.RequestsRepositoryCrud;
import co.com.crediya.requests.r2dbc.modules.status.mapper.StatusMapper;
import co.com.crediya.requests.r2dbc.modules.status.repository.StatusRepositoryCrud;
import co.com.crediya.requests.r2dbc.modules.typeloan.mapper.TypeLoanMapper;
import co.com.crediya.requests.r2dbc.modules.typeloan.repository.TypeLoanRepositoryCrud;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RequestsAdapter implements RequestsRepository {

    private static final Logger log = LoggerFactory.getLogger(RequestsAdapter.class);
    private final RequestsRepositoryCrud requestsRepositoryCrud;
    private final StatusRepositoryCrud statusRepositoryCrud;
    private final TypeLoanRepositoryCrud typeLoanRepositoryCrud;
    private final RequestsMapper requestsMapper;
    private final StatusMapper statusMapper;
    private final TypeLoanMapper typeLoanMapper;

    /**
     * Saves a request.
     *
     * <p>This method logs the request saving process in the info level.
     *
     * @param requests the request to be saved. The request cannot be null.
     * @return a {@link Mono} that emits the saved request or an error if it cannot be saved.
     */
    @Override
    public Mono<Requests> save(Requests requests) {
        log.info("Saving request by email: {}", requests.getEmail());

        return requestsRepositoryCrud.save(requestsMapper.toRequestsEntity(requests))
                .flatMap(this::buildRequestsModel)
                .doOnSuccess(r -> log.info("Request saved successfully: {}", r.getId()))
                .doOnError(error -> log.error("Error saving request: {}", error.getMessage()));
    }

    /**
     * Finds a request by its identifier.
     *
     * <p>This method logs the request finding process in the info level.
     *
     * @param id the identifier of the request to be found. The id cannot be null.
     * @return a {@link Mono} that emits the found request or an error if it cannot be found.
     */
    @Override
    public Mono<Requests> findById(Integer id) {
        log.info("Finding request by id: {}", id);
        return requestsRepositoryCrud.findById(id)
                .flatMap(this::buildRequestsModel)
                .doOnSuccess(r -> log.info("Request found successfully: {}", r.getId()))
                .doOnError(error -> log.error("Error finding request: {}", error.getMessage()));
    }

    /**
     * Finds all requests by status.
     *
     * <p>This method logs the request finding process in the info level.
     *
     * @param status the status of the requests to be found. The status cannot be null.
     * @return a {@link Flux} that emits all requests with the given status or an error if they cannot be found.
     */
    @Override
    public Flux<Requests> findByStatus(String status) {
        log.info("Finding requests by status: {}", status);
        return requestsRepositoryCrud.findByStatus(status)
                .flatMap(this::buildRequestsModel)
                .doOnNext(r -> log.info("Request found successfully whit id: {} and status: {}", r.getId(), r.getStatus()))
                .doOnComplete(() -> log.info("Requests found successfully"))
                .doOnError(error -> log.error("Error finding requests: {}", error.getMessage()));
    }

    /**
     * Finds all requests by type loan.
     *
     * <p>This method logs the request finding process in the info level.
     *
     * @param typeLoan the type loan of the requests to be found. The type loan cannot be null.
     * @return a {@link Flux} that emits all requests with the given type loan or an error if they cannot be found.
     */
    @Override
    public Flux<Requests> findByTypeLoan(String typeLoan) {
        log.info("Finding requests by type loan: {}", typeLoan);
        return requestsRepositoryCrud.findByTypeLoan(typeLoan)
                .flatMap(this::buildRequestsModel)
                .doOnNext(r -> log.info("Request found successfully whit id: {} and type loan: {}", r.getId(), r.getTypeLoan()))
                .doOnComplete(() -> log.info("Requests found successfully"))
                .doOnError(error -> log.error("Error finding requests: {}", error.getMessage()));
    }

    /**
     * Finds all requests by email.
     *
     * <p>This method logs the request finding process in the info level.
     *
     * @param email the email of the requests to be found. The email cannot be null or empty.
     * @return a {@link Flux} that emits all requests with the given email or an error if they cannot be found.
     */
    @Override
    public Flux<Requests> findByEmail(String email) {
        log.info("Finding requests by email: {}", email);
        return requestsRepositoryCrud.findByEmail(email)
                .flatMap(this::buildRequestsModel)
                .doOnNext(r -> log.info("Request found successfully whit id: {} and email: {}", r.getId(), r.getEmail()))
                .doOnComplete(() -> log.info("Requests found successfully"))
                .doOnError(error -> log.error("Error finding requests: {}", error.getMessage()));
    }

    /**
     * Builds a {@link Requests} model from a {@link RequestsEntity} and their associated entities.
     *
     * <p>This method fetches the status and type loan associated to the request and maps them to the
     * {@link Requests} model.
     *
     * @param requestsEntity the request entity to be built into a model
     * @return a {@link Mono} that emits the built {@link Requests} model
     */
    private Mono<Requests> buildRequestsModel(RequestsEntity requestsEntity) {
        return statusRepositoryCrud.findById(requestsEntity.getStatus())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.notFoundMessage(Status.class, requestsEntity.getStatus()))))
                .zipWith(typeLoanRepositoryCrud.findById(requestsEntity.getTypeLoan()))
                .map(tuple -> {
                    var status = statusMapper.toModel(tuple.getT1());
                    var typeLoan = typeLoanMapper.toTypeLoan(tuple.getT2());
                    return requestsMapper.toRequests(requestsEntity, status, typeLoan);
                })
                .doOnSuccess(requests -> log.info("Request model built successfully: {}", requests.getId()))
                .doOnError(error -> log.error("Error building request model: {}", error.getMessage()));
    }
}
