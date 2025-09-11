package co.com.crediya.requests.r2dbc.modules.loanApplication.repository;

import co.com.crediya.requests.r2dbc.modules.loanApplication.data.LoanApplicationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LoanApplicationRepositoryCrud extends ReactiveCrudRepository <LoanApplicationEntity, Integer>{
    Flux<LoanApplicationEntity> findByStatus(String status);
    Flux<LoanApplicationEntity> findByTypeLoan(String typeLoan);
    Flux<LoanApplicationEntity> findByEmail(String email);
    Flux<LoanApplicationEntity> findByStatusIn(List<String> statuses);
}
