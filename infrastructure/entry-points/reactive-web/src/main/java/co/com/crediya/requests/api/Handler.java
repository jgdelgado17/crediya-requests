package co.com.crediya.requests.api;

import co.com.crediya.requests.api.dto.LoanApplicationRequest;
import co.com.crediya.requests.api.dto.StatusRequest;
import co.com.crediya.requests.api.dto.TypeLoanRequest;
import co.com.crediya.requests.api.mapper.LoanApplicationDataMapper;
import co.com.crediya.requests.api.mapper.StatusDataMapper;
import co.com.crediya.requests.api.mapper.TypeLoanDataMapper;
import co.com.crediya.requests.usecase.loanApplication.LoanApplicationUseCase;
import co.com.crediya.requests.usecase.status.StatusUseCase;
import co.com.crediya.requests.usecase.typeloan.TypeLoanUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Handler {
    private static final Logger log = LoggerFactory.getLogger(Handler.class);
    private final Validator validator;
    private final StatusUseCase statusUseCase;
    private final TypeLoanUseCase typeLoanUseCase;
    private final LoanApplicationUseCase loanApplicationUseCase;

    /**
     * Creates a new status with the given parameters.
     *
     * <p>This method validates the status request, and if it is valid, it creates a new status.
     *
     * @param request The request to be created.
     * @return A Mono that emits a saved status or an error if the status request is not valid.
     */
    public Mono<ServerResponse> createStatus(ServerRequest request){
        log.info("Request received to create status");
        return request.bodyToMono(StatusRequest.class)
                .doOnNext(statusRequest -> {
                    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(statusRequest, "statusRequest");
                    validator.validate(statusRequest, errors);
                    if (errors.hasErrors()) {
                        List<String> errorMessages = errors.getAllErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.toList());
                        String fullErrorMessage = "Validation failed: " + String.join(", ", errorMessages);
                        throw new IllegalArgumentException(fullErrorMessage);
                    }
                })
                .map(StatusDataMapper::toStatus)
                .flatMap(statusUseCase::save)
                .flatMap(status -> ServerResponse.ok().bodyValue(status))
                .doOnSuccess(serverResponse -> log.info("Status created successfully"))
                .doOnError(e -> log.error("Error creating status: {}", e.getMessage()));
                //.onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * Creates a new type loan with the given parameters.
     *
     * <p>This method validates the type loan request, and if it is valid, it creates a new type loan.
     *
     * @param request The request to be created.
     * @return A Mono that emits a saved type loan or an error if the type loan request is not valid.
     */
    public Mono<ServerResponse> createTypeLoan(ServerRequest request){
        log.info("Request received to create type loan");
        return request.bodyToMono(TypeLoanRequest.class)
                .doOnNext(typeLoanRequest -> {
                    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(typeLoanRequest, "typeLoanRequest");
                    validator.validate(typeLoanRequest, errors);
                    if (errors.hasErrors()) {
                        List<String> errorMessages = errors.getAllErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.toList());
                        String fullErrorMessage = "Validation failed: " + String.join(", ", errorMessages);
                        throw new IllegalArgumentException(fullErrorMessage);
                    }
                })
                .map(TypeLoanDataMapper::toTypeLoan)
                .flatMap(typeLoanUseCase::createTypeLoan)
                .flatMap(typeLoan -> ServerResponse.ok().bodyValue(typeLoan))
                .doOnSuccess(serverResponse -> log.info("Type loan created successfully"))
                .doOnError(e -> log.error("Error creating type loan: {}", e.getMessage()));
                //.onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }

    /**
     * Creates a new loan application with the given parameters.
     *
     * <p>This method validates the loan application request, and if it is valid, it creates a new loan application.
     *
     * @param request The request to be created.
     * @return A Mono that emits a saved loan application or an error if the loan application request is not valid.
     */
    public Mono<ServerResponse> createLoanApplication(ServerRequest request){
        log.info("Request received to create loan application");

        String token = request.headers().firstHeader("Authorization");

        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            log.error("Authorization header is missing or malformed.");

            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", new Date());
            errorBody.put("status", 401);
            errorBody.put("error", "Unauthorized");
            errorBody.put("message", "Authorization header is missing or invalid.");
            return ServerResponse.status(401).bodyValue(errorBody);
        }

        String rawToken = token.substring(7);

        return request.bodyToMono(LoanApplicationRequest.class)
                .doOnNext(loanApplicationRequest -> {
                    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(loanApplicationRequest, "loanApplicationRequest");
                    validator.validate(loanApplicationRequest, errors);
                    if (errors.hasErrors()) {
                        List<String> errorMessages = errors.getAllErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.toList());
                        String fullErrorMessage = "Validation failed: " + String.join(", ", errorMessages);
                        throw new IllegalArgumentException(fullErrorMessage);
                    }
                })
                .map(LoanApplicationDataMapper::toLoanApplication)
                .flatMap(loanApplication -> loanApplicationUseCase.createRequest(loanApplication, rawToken))
                .map(LoanApplicationDataMapper::toLoanApplicationResponse)
                .flatMap(loanApplication -> ServerResponse.ok().bodyValue(loanApplication))
                .doOnSuccess(serverResponse -> log.info("Loan application created successfully"))
                .doOnError(e -> log.error("Error creating loan application: {}", e.getMessage()));
                //.onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}
