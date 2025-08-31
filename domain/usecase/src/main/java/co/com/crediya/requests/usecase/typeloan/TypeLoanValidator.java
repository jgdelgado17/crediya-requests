package co.com.crediya.requests.usecase.typeloan;

import co.com.crediya.requests.model.shared.enums.EnumUtils;
import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
import co.com.crediya.requests.model.typeloan.TypeLoan;
import co.com.crediya.requests.model.typeloan.TypeLoanEnum;
import reactor.core.publisher.Mono;

public class TypeLoanValidator {

    /**
     * Validates a type loan.
     *
     * <p>This method checks if the type loan has a valid name. If the name is null or empty, a
     * {@link Mono} that emits an error is returned. If the name is valid, then a {@link Mono} that
     * emits the type loan is returned. If the name is invalid, a {@link Mono} that emits an error is
     * returned.
     *
     * @param typeLoan the type loan to be validated. The type loan cannot be null.
     * @return a {@link Mono} that emits a valid type loan or an error.
     */
    public static Mono<TypeLoan> validate(TypeLoan typeLoan){
        if (typeLoan.getName() == null || typeLoan.getName().isEmpty()){
            return Mono.error(new IllegalArgumentException(ErrorMessages.requiredField("type loan name")));
        }

        try {
            EnumUtils.fromString(TypeLoanEnum.class, typeLoan.getName());
            return Mono.just(typeLoan);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }
    }

    /**
     * Validate a type loan name.
     *
     * <p>This method checks if the type loan name is valid. If the name is null or empty, a
     * {@link Mono} that emits an error is returned. If the name is valid, then a
     * {@link Mono} that emits the name is returned.
     *
     * @param name the name to be validated. The name cannot be null.
     * @return a {@link Mono} that emits a valid name or an error.
     */
    public static Mono<String> validateName(String name){
        if (name == null || name.isEmpty()){
            return Mono.error(new IllegalArgumentException(ErrorMessages.requiredField("type loan name")));
        }
        return Mono.just(name);
    }
}
