package co.com.crediya.requests.r2dbc.modules.loanApplication.mapper;

import co.com.crediya.requests.model.loanApplication.LoanApplication;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.typeloan.TypeLoan;
import co.com.crediya.requests.r2dbc.modules.loanApplication.data.LoanApplicationEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanApplicationMapper {
    /**
     * Converts a {@link LoanApplication} to a {@link LoanApplicationEntity}.
     *
     * @param loanApplication The request to be converted.
     * @return The converted request entity.
     */
    public LoanApplicationEntity toRequestsEntity(LoanApplication loanApplication){
        return LoanApplicationEntity.builder()
                .id(loanApplication.getId())
                .amount(loanApplication.getAmount())
                .term(loanApplication.getTerm())
                .email(loanApplication.getEmail())
                .typeLoan(loanApplication.getTypeLoan().getId())
                .status(loanApplication.getStatus().getId())
                .build();
    }

    /**
     * Converts a {@link LoanApplicationEntity} to a {@link LoanApplication}.
     *
     * <p>This method requires the status and type loan of the request to be provided.
     *
     * @param loanApplicationEntity The request entity to be converted.
     * @param status The status of the request.
     * @param typeLoan The type loan of the request.
     * @return The converted request.
     */
    public LoanApplication toRequests(LoanApplicationEntity loanApplicationEntity, Status status, TypeLoan typeLoan){
        return LoanApplication.builder()
                .id(loanApplicationEntity.getId())
                .amount(loanApplicationEntity.getAmount())
                .term(loanApplicationEntity.getTerm())
                .email(loanApplicationEntity.getEmail())
                .typeLoan(typeLoan)
                .status(status)
                .build();
    }
}
