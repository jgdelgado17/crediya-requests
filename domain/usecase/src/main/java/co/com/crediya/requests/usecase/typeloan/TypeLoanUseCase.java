package co.com.crediya.requests.usecase.typeloan;

import co.com.crediya.requests.model.typeloan.TypeLoan;
import co.com.crediya.requests.model.typeloan.gateways.TypeLoanRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TypeLoanUseCase {
    private final TypeLoanRepository typeLoanRepository;

    /**
     * Creates a new type loan.
     *
     * <p>This method validates the type loan and if the validation is successful, it saves the
     * type loan.
     *
     * @param typeLoan the type loan to be saved. The type loan cannot be null.
     * @return a {@link Mono} that emits a validated type loan or an error if the type loan is
     * invalid.
     * @see TypeLoanValidator#validate(TypeLoan)
     */
    public Mono<TypeLoan> createTypeLoan(TypeLoan typeLoan){
        return TypeLoanValidator.validate(typeLoan)
                .flatMap(typeLoanRepository::save);
    }

    /**
     * Finds a type loan by name.
     *
     * <p>This method validates the name and if the validation is successful, it finds the
     * type loan by name.
     *
     * @param name the name of the type loan to be found. The name cannot be empty.
     * @return a {@link Mono} that emits the found type loan or an error if the name is empty.
     * @see TypeLoanValidator#validateName(String)
     */
    public Mono<TypeLoan> findByName(String name){
        return TypeLoanValidator.validateName(name)
                .flatMap(typeLoanRepository::findByName);
    }
}
