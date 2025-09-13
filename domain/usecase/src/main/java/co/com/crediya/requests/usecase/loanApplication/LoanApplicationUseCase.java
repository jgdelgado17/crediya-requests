package co.com.crediya.requests.usecase.loanApplication;

import co.com.crediya.requests.model.loanApplication.LoanApplication;
import co.com.crediya.requests.model.loanApplication.gateways.LoanApplicationRepository;
import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
import co.com.crediya.requests.model.shared.exceptions.RecordNotFoundException;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.status.StatusEnum;
import co.com.crediya.requests.model.status.gateways.StatusRepository;
import co.com.crediya.requests.model.typeloan.gateways.TypeLoanRepository;
import co.com.crediya.requests.model.user.User;
import co.com.crediya.requests.model.user.UserLoanStatus;
import co.com.crediya.requests.model.user.gateways.UserGateway;
import co.com.crediya.requests.usecase.status.StatusValidator;
import co.com.crediya.requests.usecase.typeloan.TypeLoanValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LoanApplicationUseCase {
    private final LoanApplicationRepository loanApplicationRepository;
    private final TypeLoanRepository typeLoanRepository;
    private final StatusRepository statusRepository;
    private final UserGateway userGateway;

    /**
     * Creates a new request with the given parameters.
     *
     * <p>This method validates the amount of the request and the type loan and status, and if they exist, it creates a new request.
     *
     * @param request The request to be created.
     * @return A Mono that emits a saved request or an error if the type loan or status is not found.
     */
    public Mono<LoanApplication> createRequest(LoanApplication request, String token) {
        return userGateway.findUserByEmail(request.getEmail(), token)
                .switchIfEmpty(Mono.error(new RecordNotFoundException("User with email " + request.getEmail() + " not found in System Crediya")))
                .flatMap(user -> typeLoanRepository.findByName(request.getTypeLoan().getNames())
                        .switchIfEmpty(Mono.error(new RecordNotFoundException("Type loan " + request.getTypeLoan().getNames() + " not found in database")))
                        .flatMap(typeLoan -> LoanApplicationValidator.validateAmountInRange(
                                request.getAmount(),
                                typeLoan.getMinAmount(),
                                typeLoan.getMaxAmount()
                        ).thenReturn(typeLoan))
                        .zipWith(
                                statusRepository.findByName(StatusEnum.PENDING_REVIEW.getValue())
                                        .switchIfEmpty(Mono.error(new RecordNotFoundException("Status not found in database")))
                        )
                        .flatMap(tuple -> {
                            var typeLoan = tuple.getT1();
                            var status = tuple.getT2();
                            request.setTypeLoan(typeLoan);
                            request.setStatus(status);
                            return loanApplicationRepository.save(request);
                        }));
    }

    /**
     * Finds all requests by status.
     *
     * <p>This method validates the status names and if it is valid, it finds all requests with the
     * given status.
     *
     * @param status the status to be found. The status cannot be null or empty.
     * @return a Flux that emits all requests with the given status or an error if the status names is
     * invalid.
     * @see StatusValidator#validateName(String)
     */
    public Flux<LoanApplication> findRequestByStatus(String status) {
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
    public Flux<LoanApplication> findRequestByEmail(String email) {
        return LoanApplicationValidator.validateEmail(email)
                .flatMapMany(loanApplicationRepository::findByEmail);
    }

    /**
     * Finds all requests by type loan.
     *
     * <p>This method validates the type loan names and if it is valid, it finds all requests with the
     * given type loan.
     *
     * @param typeLoan the type loan to be found. The type loan cannot be null or empty.
     * @return a Flux that emits all requests with the given type loan or an error if the type loan names is
     * invalid.
     * @see TypeLoanValidator#validateName(String)
     */
    public Flux<LoanApplication> findRequestByTypeLoan(String typeLoan) {
        return TypeLoanValidator.validateName(typeLoan)
                .flatMapMany(loanApplicationRepository::findByTypeLoan);
    }

    /**
     * Updates the status of a request.
     *
     * <p>This method first finds a request by id and then finds a status by names.
     * If the request is not found, an error is returned. If the status is not found,
     * an error is returned. If the request and status are found, the status of the
     * request is updated.
     *
     * @param id         the id of the request to be updated.
     * @param statusName the names of the status to be updated.
     * @return a Mono that emits the updated request or an error if the request or status is not found.
     */
    public Mono<LoanApplication> updateStatusRequest(Integer id, String statusName) {
        return loanApplicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new RecordNotFoundException(ErrorMessages.notFoundMessage(LoanApplication.class, id))))
                .zipWith(StatusValidator.validateName(statusName)
                        .flatMap(status -> statusRepository.findByName(statusName)
                                .switchIfEmpty(Mono.error(new RecordNotFoundException(ErrorMessages.notFoundMessage(Status.class, statusName))))
                        ))
                .flatMap(tuple -> {
                    var request = tuple.getT1();
                    var status = tuple.getT2();
                    request.setStatus(status);
                    return loanApplicationRepository.save(request);
                });
    }

    /**
     * Finds all requests for manual review.
     *
     * <p>This method finds all requests with the status PENDING_REVIEW, REJECTED, or MANUAL_REVIEW.
     * It then finds the users associated with each request and returns a Flux of UserLoanStatus objects.
     *
     * @param page the page number to be used for pagination.
     * @param size the number of requests to be returned per page.
     * @param token the token to be used for authentication.
     * @return a Flux that emits UserLoanStatus objects or an error if the requests or users are not found.
     */
    public Flux<UserLoanStatus> findRequestsForManualReview(int page, int size, String token) {
        List<String> statusNames = List.of(
                StatusEnum.PENDING_REVIEW.getValue(),
                StatusEnum.REJECTED.getValue(),
                StatusEnum.MANUAL_REVIEW.getValue()
        );

        int offset = page * size;

        return loanApplicationRepository.findByStatusNamesInAndPaginate(statusNames, offset, size)
                .collectList()
                .flatMapMany(loanApplications -> {
                    if (loanApplications.isEmpty()) {
                        return Flux.empty();
                    }

                    List<String> uniqueEmails = loanApplications.stream()
                            .map(LoanApplication::getEmail)
                            .distinct()
                            .collect(Collectors.toList());

                    return userGateway.findUsersByEmails(uniqueEmails, token)
                            .collectMap(User::getEmail)
                            .flatMapMany(userMap ->
                                    Flux.fromIterable(loanApplications)
                                            .map(loanApplication -> {
                                                User user = userMap.get(loanApplication.getEmail());
                                                return UserLoanStatus.builder()
                                                        .idLoanApplication(loanApplication.getId())
                                                        .name(user.getName())
                                                        .email(user.getEmail())
                                                        .documentNumber(user.getDocumentNumber())
                                                        .baseSalary(user.getBaseSalary())
                                                        //.totalMonthlyDebt(0.0f) //TODO
                                                        .loanStatus(loanApplication.getStatus().getNames())
                                                        .loanType(loanApplication.getTypeLoan().getNames())
                                                        .loanAmount(loanApplication.getAmount())
                                                        .loanTerm(loanApplication.getTerm())
                                                        .loanInterestRate(loanApplication.getTypeLoan().getInterestRate())
                                                        .build();
                                            })
                            );
                });
    }
}
