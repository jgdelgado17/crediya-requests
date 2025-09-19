package co.com.crediya.requests.api;

import co.com.crediya.requests.api.dto.*;
import co.com.crediya.requests.api.mapper.LoanApplicationDataMapper;
import co.com.crediya.requests.api.mapper.StatusDataMapper;
import co.com.crediya.requests.api.mapper.TypeLoanDataMapper;
import co.com.crediya.requests.model.shared.exceptions.UnauthorizedException;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.typeloan.TypeLoan;
import co.com.crediya.requests.model.user.UserLoanStatus;
import co.com.crediya.requests.usecase.loanApplication.LoanApplicationUseCase;
import co.com.crediya.requests.usecase.status.StatusUseCase;
import co.com.crediya.requests.usecase.typeloan.TypeLoanUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
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

    @Operation(
            tags = {"Status"},
            operationId = "createStatus",
            description = "Create a new status if it does not exist",
            summary = "Create a new status",
            requestBody = @RequestBody(
                    description = "Status to be created",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StatusRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Example Status PENDING_REVIEW",
                                            summary = "Example Status PENDING_REVIEW",
                                            value = "{\"name\": \"PENDING_REVIEW\"," +
                                                    "\"description\": \"Pending review\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Example Status MANUAL_REVIEW",
                                            summary = "Example Status MANUAL_REVIEW",
                                            value = "{\"name\": \"MANUAL_REVIEW\"," +
                                                    "\"description\": \"Manual review\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Example Status APPROVED",
                                            summary = "Example Status APPROVED",
                                            value = "{\"name\": \"APPROVED\"," +
                                                    "\"description\": \"Approved\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Example Status REJECTED",
                                            summary = "Example Status REJECTED",
                                            value = "{\"name\": \"REJECTED\"," +
                                                    "\"description\": \"Rejected\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Status created successfully",
                            content = @Content(
                                    schema = @Schema(implementation = Status.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    )
            }
    )
    public Mono<ServerResponse> createStatus(ServerRequest request) {
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

    @Operation(
            tags = {"TypeLoan"},
            operationId = "createTypeLoan",
            description = "Create a new type loan if it does not exist",
            summary = "Create a new type loan",
            requestBody = @RequestBody(
                    description = "Type loan to be created",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TypeLoanRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Example PERSONAL",
                                            summary = "Example PERSONAL",
                                            value = "{\"name\": \"PERSONAL\",\"description\": \"Personal\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Example CONSUMER",
                                            summary = "Example CONSUMER",
                                            value = "{\"name\": \"CONSUMER\",\"description\": \"Consumer\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Example MORTGAGE",
                                            summary = "Example MORTGAGE",
                                            value = "{\"name\": \"MORTGAGE\",\"description\": \"Mortgage\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Example AUTOMOTIVE",
                                            summary = "Example AUTOMOTIVE",
                                            value = "{\"name\": \"AUTOMOTIVE\",\"description\": \"Automotive\"}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Type loan created successfully",
                            content = @Content(
                                    schema = @Schema(implementation = TypeLoan.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    )
            }
    )
    public Mono<ServerResponse> createTypeLoan(ServerRequest request) {
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

    @Operation(
            tags = {"LoanApplication"},
            operationId = "createLoanApplication",
            description = "Create a new loan application if it does not exist",
            summary = "Create a new loan application",
            requestBody = @RequestBody(
                    description = "Loan application to be created",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoanApplicationRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Loan application created successfully",
                            content = @Content(
                                    schema = @Schema(implementation = LoanApplicationResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    )
            }
    )
    public Mono<ServerResponse> createLoanApplication(ServerRequest request) {
        log.info("Request received to create loan application");

        return extractAndValidateToken(request)
                .flatMap(rawToken -> request.bodyToMono(LoanApplicationRequest.class)
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
                )
                .doOnError(e -> log.error("Error creating loan application: {}", e.getMessage()));
    }

    @Operation(
            tags = {"LoanApplication"},
            operationId = "listManualReviewRequests",
            summary = "List loan applications for manual review",
            description = "Retrieves a paginated list of loan applications that require manual review.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "page",
                            description = "Page number (starting from 0)",
                            required = false,
                            schema = @Schema(type = "integer", defaultValue = "0")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "size",
                            description = "Number of items per page",
                            required = false,
                            schema = @Schema(type = "integer", defaultValue = "10")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of loan applications for manual review",
                            content = @Content(
                                    schema = @Schema(implementation = UserLoanStatus.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid page or size parameters",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    )
            }
    )
    public Mono<ServerResponse> listManualReviewRequests(ServerRequest request) {
        log.info("Request received to list manual review requests");

        int page = request.queryParam("page")
                .map(Integer::parseInt)
                .orElse(0);

        int size = request.queryParam("size")
                .map(Integer::parseInt)
                .orElse(10);

        if (page < 0 || size <= 0) {
            log.error("Invalid page or size parameters");
            return Mono.error(new IllegalArgumentException("Page must be non-negative and size must be positive."));
        }

        return extractAndValidateToken(request)
                .flatMap(rawToken -> loanApplicationUseCase.findRequestsForManualReview(page, size, rawToken)
                        .collectList()
                        .flatMap(responseList -> ServerResponse.ok().bodyValue(responseList))
                        .doOnSuccess(serverResponse -> log.info("Manual review requests listed successfully"))
                )
                .doOnError(e -> log.error("Error listing manual review requests: {}", e.getMessage()));
    }

    @Operation(
            tags = {"LoanApplication"},
            operationId = "updateStatusRequest",
            summary = "Update the status of a loan application",
            description = "Updates the status of a loan application based on the provided request ID and status name.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Update status request",
                    content = @Content(schema = @Schema(implementation = UpdateStatusLoanApplicationRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Loan application status updated successfully",
                            content = @Content(schema = @Schema(implementation = LoanApplicationResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = Error.class))
                    )
            }
    )
    public Mono<ServerResponse> updateStatusRequest(ServerRequest request) {
        log.info("Request received to update status request");

        return request.bodyToMono(UpdateStatusLoanApplicationRequest.class)
                .doOnNext(updateStatusLoanApplicationRequest -> {
                    BeanPropertyBindingResult errors = new BeanPropertyBindingResult(updateStatusLoanApplicationRequest, "updateStatusLoanApplicationRequest");
                    validator.validate(updateStatusLoanApplicationRequest, errors);
                    if (errors.hasErrors()) {
                        List<String> errorMessages = errors.getAllErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.toList());
                        String fullErrorMessage = "Validation failed: " + String.join(", ", errorMessages);
                        throw new IllegalArgumentException(fullErrorMessage);
                    }
                })
                .flatMap(updateStatusLoanApplicationRequest -> {
                    Integer idLoanApplication = updateStatusLoanApplicationRequest.idLoanApplication();
                    String status = updateStatusLoanApplicationRequest.statusName();
                    return loanApplicationUseCase.updateStatusRequest(idLoanApplication, status);
                })
                .map(LoanApplicationDataMapper::toLoanApplicationResponse)
                .flatMap(loanApplication -> ServerResponse.ok().bodyValue(loanApplication))
                .doOnSuccess(serverResponse -> log.info("Loan application status updated successfully"))
                .doOnError(e -> log.error("Error updating loan application status: {}", e.getMessage()));
    }

    private Mono<String> extractAndValidateToken(ServerRequest request) {
        String token = request.headers().firstHeader("Authorization");

        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            log.error("Authorization header is missing or malformed.");
            return Mono.error(new UnauthorizedException("Authorization header is missing or invalid."));
        }

        String rawToken = token.substring(7);
        return Mono.just(rawToken);
    }
}
