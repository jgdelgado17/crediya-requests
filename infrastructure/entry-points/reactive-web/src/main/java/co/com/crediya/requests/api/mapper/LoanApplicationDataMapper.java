package co.com.crediya.requests.api.mapper;

import co.com.crediya.requests.api.dto.LoanApplicationRequest;
import co.com.crediya.requests.api.dto.LoanApplicationResponse;
import co.com.crediya.requests.model.loanApplication.LoanApplication;
import co.com.crediya.requests.model.typeloan.TypeLoan;

public class LoanApplicationDataMapper {
    public static LoanApplication toLoanApplication(LoanApplicationRequest loanApplicationRequest){
        return LoanApplication.builder()
                .amount(loanApplicationRequest.amount())
                .term(loanApplicationRequest.term())
                .email(loanApplicationRequest.email())
                .typeLoan(TypeLoan.builder().names(loanApplicationRequest.typeLoan()).build())
                .build();
    }

    public static LoanApplicationResponse toLoanApplicationResponse(LoanApplication loanApplication){
        return new LoanApplicationResponse(
                loanApplication.getId(),
                loanApplication.getAmount(),
                loanApplication.getTerm(),
                loanApplication.getEmail(),
                loanApplication.getStatus().getNames(),
                loanApplication.getTypeLoan().getNames()
        );
    }
}
