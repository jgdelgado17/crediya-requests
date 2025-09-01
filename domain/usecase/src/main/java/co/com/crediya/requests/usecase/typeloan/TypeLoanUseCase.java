package co.com.crediya.requests.usecase.typeloan;

import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
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
     * <p>This method validates the type loan to be created and if it is valid, it creates the type loan.
     * If the type loan already exists, an error is returned.
     *
     * @param typeLoan the type loan to be created.
     * @return a Mono that emits a saved type loan or an error if the type loan already exists.
     */
    public Mono<TypeLoan> createTypeLoan(TypeLoan typeLoan){
        return TypeLoanValidator.validate(typeLoan)
                .flatMap(validTypeLoan ->
                        typeLoanRepository.findByName(typeLoan.getName())
                        .flatMap(existingTypeLoan ->
                                Mono.error(new IllegalArgumentException(ErrorMessages.objectAlreadyExists(typeLoan.getName()))).cast(TypeLoan.class)
                        )
                                .switchIfEmpty(Mono.defer(() -> typeLoanRepository.save(typeLoan)))
                );
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
