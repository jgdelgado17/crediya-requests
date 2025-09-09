package co.com.crediya.requests.api.exceptionHandler;

import co.com.crediya.requests.model.shared.exceptions.RecordNotFoundException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                          WebProperties webProperties,
                                          ApplicationContext applicationContext,
                                          ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext); // <--- Se accede a getResources()
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "Internal Server Error";

        if (error instanceof RecordNotFoundException){
            httpStatus = HttpStatus.NOT_FOUND;
            errorMessage = error.getMessage();
        } else if (error instanceof WebExchangeBindException bindException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            List<String> errors = bindException.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            errorMessage = "Validation failed: " + String.join(", ", errors);
        } else if (error instanceof IllegalArgumentException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            errorMessage = error.getMessage();
        } else if (error instanceof ServerWebInputException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            errorMessage = "Invalid JSON format or data type mismatch. Please check your input.";
        } else if (error instanceof RuntimeException){
            httpStatus = HttpStatus.BAD_REQUEST;
            errorMessage = error.getMessage();
        }

        Map<String, Object> errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        //Map<String, Object> errorAttributes = getErrorAttributes(request, ErrorAttributeOptions.of(Include.STACK_TRACE));
        errorAttributes.put("status", httpStatus.value());
        errorAttributes.put("error", httpStatus.getReasonPhrase());
        errorAttributes.put("message", errorMessage);
        errorAttributes.remove("exception");
        errorAttributes.remove("path");
        errorAttributes.remove("requestId");

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorAttributes));
    }
}
