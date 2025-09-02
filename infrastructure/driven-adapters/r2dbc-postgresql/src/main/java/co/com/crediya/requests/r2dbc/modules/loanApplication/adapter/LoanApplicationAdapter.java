package co.com.crediya.requests.r2dbc.modules.loanApplication.adapter;

import co.com.crediya.requests.model.loanApplication.LoanApplication;
import co.com.crediya.requests.model.loanApplication.gateways.LoanApplicationRepository;
import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.r2dbc.modules.loanApplication.data.LoanApplicationEntity;
import co.com.crediya.requests.r2dbc.modules.loanApplication.mapper.LoanApplicationMapper;
import co.com.crediya.requests.r2dbc.modules.loanApplication.repository.LoanApplicationRepositoryCrud;
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
public class LoanApplicationAdapter implements LoanApplicationRepository {

    private static final Logger log = LoggerFactory.getLogger(LoanApplicationAdapter.class);
    private final LoanApplicationRepositoryCrud loanApplicationRepositoryCrud;
    private final StatusRepositoryCrud statusRepositoryCrud;
    private final TypeLoanRepositoryCrud typeLoanRepositoryCrud;
    private final LoanApplicationMapper loanApplicationMapper;
    private final StatusMapper statusMapper;
    private final TypeLoanMapper typeLoanMapper;

    /**
     * Saves a request.
     *
     * <p>This method logs the request saving process in the info level.
     *
     * @param loanApplication the request to be saved. The request cannot be null.
     * @return a {@link Mono} that emits the saved request or an error if it cannot be saved.
     */
    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        log.info("Saving request by email: {}", loanApplication.getEmail());

        return loanApplicationRepositoryCrud.save(loanApplicationMapper.toRequestsEntity(loanApplication))
                .flatMap(this::buildRequestsModel)
                .doOnSuccess(r -> log.info("Request saved successfully: {}", r.getId()))
                .doOnError(error -> log.error("Error saving request: {}", error.getMessage()))
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
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
    public Mono<LoanApplication> findById(Integer id) {
        log.info("Finding request by id: {}", id);
        return loanApplicationRepositoryCrud.findById(id)
                .flatMap(this::buildRequestsModel)
                .doOnSuccess(r -> log.info("Request found successfully: {}", r.getId()))
                .doOnError(error -> log.error("Error finding request: {}", error.getMessage()))
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
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
    public Flux<LoanApplication> findByStatus(String status) {
        log.info("Finding requests by status: {}", status);
        return loanApplicationRepositoryCrud.findByStatus(status)
                .flatMap(this::buildRequestsModel)
                .doOnNext(r -> log.info("Request found successfully whit id: {} and status: {}", r.getId(), r.getStatus()))
                .doOnComplete(() -> log.info("LoanApplication found successfully"))
                .doOnError(error -> log.error("Error finding requests: {}", error.getMessage()))
                .onErrorResume(error -> Flux.error(new RuntimeException(error.getMessage())));
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
    public Flux<LoanApplication> findByTypeLoan(String typeLoan) {
        log.info("Finding requests by type loan: {}", typeLoan);
        return loanApplicationRepositoryCrud.findByTypeLoan(typeLoan)
                .flatMap(this::buildRequestsModel)
                .doOnNext(r -> log.info("Request found successfully whit id: {} and type loan: {}", r.getId(), r.getTypeLoan()))
                .doOnComplete(() -> log.info("LoanApplication found successfully"))
                .doOnError(error -> log.error("Error finding requests: {}", error.getMessage()))
                .onErrorResume(error -> Flux.error(new RuntimeException(error.getMessage())));
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
    public Flux<LoanApplication> findByEmail(String email) {
        log.info("Finding requests by email: {}", email);
        return loanApplicationRepositoryCrud.findByEmail(email)
                .flatMap(this::buildRequestsModel)
                .doOnNext(r -> log.info("Request found successfully whit id: {} and email: {}", r.getId(), r.getEmail()))
                .doOnComplete(() -> log.info("LoanApplication found successfully"))
                .doOnError(error -> log.error("Error finding requests: {}", error.getMessage()))
                .onErrorResume(error -> Flux.error(new RuntimeException(error.getMessage())));
    }

    /**
     * Builds a {@link LoanApplication} model from a {@link LoanApplicationEntity} and their associated entities.
     *
     * <p>This method fetches the status and type loan associated to the request and maps them to the
     * {@link LoanApplication} model.
     *
     * @param loanApplicationEntity the request entity to be built into a model
     * @return a {@link Mono} that emits the built {@link LoanApplication} model
     */
    private Mono<LoanApplication> buildRequestsModel(LoanApplicationEntity loanApplicationEntity) {
        return statusRepositoryCrud.findById(loanApplicationEntity.getStatus())
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.notFoundMessage(Status.class, loanApplicationEntity.getStatus()))))
                .zipWith(typeLoanRepositoryCrud.findById(loanApplicationEntity.getTypeLoan()))
                .map(tuple -> {
                    var status = statusMapper.toModel(tuple.getT1());
                    var typeLoan = typeLoanMapper.toTypeLoan(tuple.getT2());
                    return loanApplicationMapper.toRequests(loanApplicationEntity, status, typeLoan);
                })
                .doOnSuccess(loanApplication -> log.info("Request model built successfully: {}", loanApplication.getId()))
                .doOnError(error -> log.error("Error building request model: {}", error.getMessage()))
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
    }
}
