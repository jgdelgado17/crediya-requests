package co.com.crediya.requests.model.typeloan.gateways;

import co.com.crediya.requests.model.typeloan.TypeLoan;
import reactor.core.publisher.Mono;

public interface TypeLoanRepository {
    Mono<TypeLoan> save(TypeLoan typeLoan);
    Mono<TypeLoan> findByName(String name);
}
