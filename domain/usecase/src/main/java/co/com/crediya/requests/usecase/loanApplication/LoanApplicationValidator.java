package co.com.crediya.requests.usecase.loanApplication;

import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

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

    public static Mono<BigDecimal> validateAmount(BigDecimal amount){
        if(amount == null){
            return Mono.error(new IllegalArgumentException(ErrorMessages.requiredField("amount")));
        }
        return Mono.just(amount);
    }

    public static Mono<BigDecimal> validateAmountMin(BigDecimal amount, BigDecimal minAmount) {
        return validateAmount(amount)
                .flatMap(validAmount -> {
                    if (validAmount.compareTo(minAmount) < 0) {
                        return Mono.error(new IllegalArgumentException("The amount must be greater than or equal to " + minAmount));
                    }
                    return Mono.just(validAmount);
                });
    }

    public static Mono<BigDecimal> validateAmountMax(BigDecimal amount, BigDecimal maxAmount) {
        return validateAmount(amount)
                .flatMap(validAmount -> {
                    if (validAmount.compareTo(maxAmount) > 0) {
                        return Mono.error(new IllegalArgumentException("The amount must be less than or equal to " + maxAmount));
                    }
                    return Mono.just(validAmount);
                });
    }

    public static Mono<BigDecimal> validateAmountInRange(BigDecimal amount, BigDecimal minAmount, BigDecimal maxAmount) {
        return validateAmount(amount)
                .flatMap(validAmount -> validateAmountMin(validAmount, minAmount))
                .flatMap(validAmount -> validateAmountMax(validAmount, maxAmount));
    }
}
