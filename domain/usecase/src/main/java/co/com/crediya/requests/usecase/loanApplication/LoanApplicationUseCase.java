package co.com.crediya.requests.usecase.loanApplication;

import co.com.crediya.requests.model.loanApplication.LoanApplication;
import co.com.crediya.requests.model.loanApplication.gateways.LoanApplicationRepository;
import co.com.crediya.requests.model.notification.NotificationRequest;
import co.com.crediya.requests.model.notification.gateways.NotificationGateway;
import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
import co.com.crediya.requests.model.shared.exceptions.RecordNotFoundException;
import co.com.crediya.requests.model.shared.exceptions.UnauthorizedException;
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
    private final NotificationGateway notificationGateway;

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
     * Updates the status of a loan application.
     *
     * <p>This method validates the id and status name and if they are valid, it updates the status of the loan application.
     * Send a message to SQS to notify the user.
     *
     * @param id the id of loan application to update.
     * @param statusName new status of loan application.
     * @param authenticatedUserEmail email of authenticated user.
     * @param token token of authenticated user.
     * @return updated loan application.
     */
    public Mono<LoanApplication> updateStatusRequest(Integer id, String statusName, String authenticatedUserEmail, String token) {
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

                    if (request.getEmail().equals(authenticatedUserEmail)) {
                        return Mono.error(new UnauthorizedException("The user cannot change the status of a loan application that belongs to him."));
                    }

                    return userGateway.findUserByEmail(request.getEmail(), token)
                            .switchIfEmpty(Mono.error(new RecordNotFoundException(ErrorMessages.notFoundMessage(User.class, request.getEmail()))))
                            .flatMap(user -> loanApplicationRepository.save(request)
                                    .flatMap(updatedRequest -> {
                                        NotificationRequest notification = NotificationRequest.builder()
                                                .applicantEmail(updatedRequest.getEmail())
                                                .applicantName(user.getName().concat(" ").concat(user.getLastName()))
                                                .status(updatedRequest.getStatus().getNames())
                                                .loanAmount(updatedRequest.getAmount())
                                                .build();
                                        return notificationGateway.sendNotification(notification)
                                                .thenReturn(updatedRequest);
                                    }));
                });
    }

    /**
     * Finds all requests for manual review.
     *
     * <p>This method finds all requests with status PENDING_REVIEW, REJECTED, or MANUAL_REVIEW.
     * It then finds all users associated with these requests and returns a Flux of UserLoanStatus objects.
     *
     * @param page  the page of requests to be found.
     * @param size  the size of the page of requests to be found.
     * @param token the token to be used for authentication.
     * @return a Flux of UserLoanStatus objects.
     */
    public Flux<UserLoanStatus> findRequestsForManualReview(int page, int size, String token) {
        List<String> statusNames = List.of(
                StatusEnum.PENDING_REVIEW.getValue(),
                StatusEnum.REJECTED.getValue(),
                StatusEnum.MANUAL_REVIEW.getValue()
        );

        Flux<String> statusIdsFlux = Flux.fromIterable(statusNames)
                .flatMap(statusRepository::findByName)
                .map(status -> status.getId().toString());

        return statusIdsFlux.collectList()
                .flatMapMany(listStatusIds ->
                        loanApplicationRepository.findByStatusIn(listStatusIds, page, size)
                                .collectList()
                                .flatMapMany(listLoanApplications -> {
                                    if (listLoanApplications.isEmpty()) {
                                        return Flux.empty();
                                    }

                                    List<String> uniqueEmails = listLoanApplications.stream()
                                            .map(LoanApplication::getEmail)
                                            .distinct()
                                            .collect(Collectors.toList());

                                    return userGateway.findUsersByEmails(uniqueEmails, token)
                                            .collectMap(User::getEmail)
                                            .flatMapMany(userMap ->
                                                    Flux.fromIterable(listLoanApplications)
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
                                }));
    }
}
