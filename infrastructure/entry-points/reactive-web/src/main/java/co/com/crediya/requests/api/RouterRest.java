package co.com.crediya.requests.api;

import co.com.crediya.requests.api.dto.LoanApplicationRequest;
import co.com.crediya.requests.api.dto.LoanApplicationResponse;
import co.com.crediya.requests.api.dto.StatusRequest;
import co.com.crediya.requests.api.dto.TypeLoanRequest;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.typeloan.TypeLoan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/status",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createStatus",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    operation = @Operation(
                            tags = {"Status"},
                            operationId = "createStatus",
                            description = "Create a new status if it does not exist",
                            summary = "Create a new status",
                            requestBody = @RequestBody(
                                    description = "Status to be created",
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = StatusRequest.class)
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
            ),
            @RouterOperation(
                    path = "/api/v1/type-loan",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createTypeLoan",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    operation = @Operation(
                            tags = {"TypeLoan"},
                            operationId = "createTypeLoan",
                            description = "Create a new type loan if it does not exist",
                            summary = "Create a new type loan",
                            requestBody = @RequestBody(
                                    description = "Type loan to be created",
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = TypeLoanRequest.class)
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
            ),
            @RouterOperation(
                    path = "/api/v1/loan-application",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createLoanApplication",
                    produces = {MediaType.APPLICATION_JSON_VALUE},
                    operation = @Operation(
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
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(schema = @Schema(implementation = Error.class))
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/status"), handler::createStatus)
                .andRoute(POST("/api/v1/type-loan"), handler::createTypeLoan)
                .andRoute(POST("/api/v1/loan-application"), handler::createLoanApplication);
    }
}
