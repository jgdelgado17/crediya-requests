package co.com.crediya.requests.usecase.loanApplication;

import co.com.crediya.requests.model.loanApplication.LoanApplication;
import co.com.crediya.requests.model.loanApplication.gateways.LoanApplicationRepository;
import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.status.StatusEnum;
import co.com.crediya.requests.model.status.gateways.StatusRepository;
import co.com.crediya.requests.model.typeloan.gateways.TypeLoanRepository;
import co.com.crediya.requests.usecase.status.StatusValidator;
import co.com.crediya.requests.usecase.typeloan.TypeLoanValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final TypeLoanRepository typeLoanRepository;
    private final StatusRepository statusRepository;

    /**
     * Creates a new request with the given parameters.
     *
     * <p>This method validates the amount of the request and the type loan and status, and if they exist, it creates a new request.
     *
     * @param request The request to be created.
     * @return A Mono that emits a saved request or an error if the type loan or status is not found.
     */
    public Mono<LoanApplication> createRequest(LoanApplication request) {
        return typeLoanRepository.findByName(request.getTypeLoan().getName())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Type loan not found in database")))
                .flatMap(typeLoan -> LoanApplicationValidator.validateAmountInRange(
                        request.getAmount(),
                        typeLoan.getMinAmount(),
                        typeLoan.getMaxAmount()
                ).thenReturn(typeLoan))
                .zipWith(
                        statusRepository.findByName(StatusEnum.PENDING_REVIEW.getValue())
                                .switchIfEmpty(Mono.error(new Exception("Status not found in database")))
                )
                .flatMap(tuple -> {
                    var typeLoan = tuple.getT1();
                    var status = tuple.getT2();
                    request.setTypeLoan(typeLoan);
                    request.setStatus(status);
                    return loanApplicationRepository.save(request);
                });
    }

    /**
     * Finds all requests by status.
     *
     * <p>This method validates the status name and if it is valid, it finds all requests with the
     * given status.
     *
     * @param status the status to be found. The status cannot be null or empty.
     * @return a Flux that emits all requests with the given status or an error if the status name is
     * invalid.
     * @see StatusValidator#validateName(String)
     */
    public Flux<LoanApplication> findRequestByStatus(String status){
        return StatusValidator.validateName(status)
                .flatMapMany(loanApplicationRepository::findByStatus);
    }

    /**
     * Finds all requests by email.
     *
     * <p>This method validates the email and if it is valid, it finds all requests with the
     * given email.
     *
     * @param email the email to be found. The email cannot be null or empty.
     * @return a Flux that emits all requests with the given email or an error if the email is
     * invalid.
     * @see LoanApplicationValidator#validateEmail(String)
     */
    public Flux<LoanApplication> findRequestByEmail(String email){
        return LoanApplicationValidator.validateEmail(email)
                .flatMapMany(loanApplicationRepository::findByEmail);
    }

    /**
     * Finds all requests by type loan.
     *
     * <p>This method validates the type loan name and if it is valid, it finds all requests with the
     * given type loan.
     *
     * @param typeLoan the type loan to be found. The type loan cannot be null or empty.
     * @return a Flux that emits all requests with the given type loan or an error if the type loan name is
     * invalid.
     * @see TypeLoanValidator#validateName(String)
     */
    public Flux<LoanApplication> findRequestByTypeLoan(String typeLoan){
        return TypeLoanValidator.validateName(typeLoan)
                .flatMapMany(loanApplicationRepository::findByTypeLoan);
    }

    /**
     * Updates the status of a request.
     *
     * <p>This method first finds a request by id and then finds a status by name.
     * If the request is not found, an error is returned. If the status is not found,
     * an error is returned. If the request and status are found, the status of the
     * request is updated.
     *
     * @param id the id of the request to be updated.
     * @param statusName the name of the status to be updated.
     * @return a Mono that emits the updated request or an error if the request or status is not found.
     */
    public Mono<LoanApplication> updateStatusRequest(Integer id, String statusName) {
        return loanApplicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.notFoundMessage(LoanApplication.class, id))))
                .zipWith(StatusValidator.validateName(statusName)
                        .flatMap(status -> statusRepository.findByName(statusName)
                                .switchIfEmpty(Mono.error(new IllegalArgumentException(ErrorMessages.notFoundMessage(Status.class, statusName))))
                ))
                .flatMap(tuple -> {
                    var request = tuple.getT1();
                    var status = tuple.getT2();
                    request.setStatus(status);
                    return loanApplicationRepository.save(request);
                });
    }
}
