package co.com.crediya.requests.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
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
                    produces = {MediaType.APPLICATION_JSON_VALUE}
            ),
            @RouterOperation(
                    path = "/api/v1/type-loan",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createTypeLoan",
                    produces = {MediaType.APPLICATION_JSON_VALUE}
            ),
            @RouterOperation(
                    path = "/api/v1/loan-application",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createLoanApplication",
                    produces = {MediaType.APPLICATION_JSON_VALUE}
            ),
            @RouterOperation(
                    path = "/api/v1/loan-application/manual-review",
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listManualReviewRequests",
                    produces = {MediaType.APPLICATION_JSON_VALUE}
            ),
            @RouterOperation(
                    path = "/api/v1/loan-application/update-status",
                    method = RequestMethod.PUT,
                    beanClass = Handler.class,
                    beanMethod = "updateStatusRequest",
                    produces = {MediaType.APPLICATION_JSON_VALUE}
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/status"), handler::createStatus)
                .andRoute(POST("/api/v1/type-loan"), handler::createTypeLoan)
                .andRoute(POST("/api/v1/loan-application"), handler::createLoanApplication)
                .andRoute(GET("/api/v1/loan-application/manual-review"), handler::listManualReviewRequests)
                .andRoute(PUT("/api/v1/loan-application/update-status"), handler::updateStatusRequest);
    }
}
