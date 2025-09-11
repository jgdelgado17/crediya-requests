package co.com.crediya.requests.model.loanApplication.gateways;

import co.com.crediya.requests.model.loanApplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanApplicationRepository {
    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<LoanApplication> findById(Integer id);
    Flux<LoanApplication> findByStatus(String status);
    Flux<LoanApplication> findByTypeLoan(String typeLoan);
    Flux<LoanApplication> findByEmail(String email);
    Flux<LoanApplication> findByStatusIn(List<String> statuses);
}
