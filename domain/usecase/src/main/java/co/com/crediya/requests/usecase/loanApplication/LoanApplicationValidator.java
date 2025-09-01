package co.com.crediya.requests.usecase.loanApplication;

import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
import reactor.core.publisher.Mono;

public class LoanApplicationValidator {

    /**
     * Validates an email.
     *
     * <p>This method checks if the email is null or empty. If the email is null or empty,
     * a {@link Mono} that emits an error is returned. If the email is valid, then a
     * {@link Mono} that emits the email is returned.
     *
     * @param email the email to be validated. The email cannot be null.
     * @return a {@link Mono} that emits a valid email or an error.
     */
    public static Mono<String> validateEmail(String email){
        if(email == null || email.isEmpty()){
            return Mono.error(new IllegalArgumentException(ErrorMessages.requiredField("email")));
        }
        return Mono.just(email);
    }
}
