package co.com.crediya.requests.security.config;

import co.com.crediya.requests.security.repository.SecurityContextRepository;
import co.com.crediya.requests.security.service.AuthenticationManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final static Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint(this::authenticationEntryPoint)
                        .accessDeniedHandler(this::accessDeniedHandler)
                )
                .authorizeExchange()
                .pathMatchers("/actuator").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/favicon.ico").permitAll()
                .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-ui.html/**", "/webjars/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/v1/status").hasRole("ADMIN")
                .pathMatchers(HttpMethod.POST, "/api/v1/type-loan").hasAnyRole("ADMIN")
                .pathMatchers(HttpMethod.GET, "/api/v1/loan-application/manual-review").hasAnyRole( "ADVISOR")
                .pathMatchers(HttpMethod.PUT, "/api/v1/loan-application/update-status").hasAnyRole("ADVISOR")
                .anyExchange().authenticated()
                .and()
                .build();
    }

    private Mono<Void> accessDeniedHandler(ServerWebExchange exchange, AccessDeniedException e) {
        logger.error("Access denied to {}", exchange.getRequest().getPath());
        return createJsonErrorResponse(exchange, HttpStatus.FORBIDDEN, "Forbidden", "You do not have enough permissions to access this resource");
    }

    private Mono<Void> authenticationEntryPoint(ServerWebExchange exchange, AuthenticationException e) {
        logger.error("Unauthorized access to {}", exchange.getRequest().getPath());
        return createJsonErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized", "Authentication failed or token is invalid");
    }

    private Mono<Void> createJsonErrorResponse(ServerWebExchange exchange, HttpStatus httpStatus, String error, String message) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        errorAttributes.put("timestamp", Instant.now().toString());
        errorAttributes.put("status", httpStatus.value());
        errorAttributes.put("error", error);
        errorAttributes.put("message", message);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorAttributes);
            exchange.getResponse().setStatusCode(httpStatus);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
        } catch (JsonProcessingException e) {
            // Fallback to a simple response if JSON serialization fails
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }
}
