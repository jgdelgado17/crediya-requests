package co.com.crediya.requests.api;

import co.com.crediya.requests.api.dto.StatusRequest;
import co.com.crediya.requests.api.mapper.StatusDataMapper;
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

import java.util.List;
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
                .doOnSuccess(status -> log.info("Status created successfully: {}", status))
                .doOnError(e -> log.error("Error creating status: {}", e.getMessage()))
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue(e.getMessage()));
    }
}
