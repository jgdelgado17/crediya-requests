package co.com.crediya.requests.r2dbc.modules.typeloan.mapper;

import co.com.crediya.requests.model.typeloan.TypeLoan;
import co.com.crediya.requests.r2dbc.modules.typeloan.data.TypeLoanEntity;
import org.springframework.stereotype.Component;

@Component
public class TypeLoanMapper {
    public TypeLoan toTypeLoan(TypeLoanEntity typeLoanEntity){
        return TypeLoan.builder()
                .id(typeLoanEntity.getId())
                .name(typeLoanEntity.getNames())
                .minAmount(typeLoanEntity.getMinAmount())
                .maxAmount(typeLoanEntity.getMaxAmount())
                .interestRate(typeLoanEntity.getInterestRate())
                .automaticValidation(typeLoanEntity.getAutomaticValidation())
                .build();
    }

    public TypeLoanEntity toTypeLoanEntity(TypeLoan typeLoan){
        return TypeLoanEntity.builder()
                .id(typeLoan.getId())
                .names(typeLoan.getName())
                .minAmount(typeLoan.getMinAmount())
                .maxAmount(typeLoan.getMaxAmount())
                .interestRate(typeLoan.getInterestRate())
                .automaticValidation(typeLoan.getAutomaticValidation())
                .build();
    }
}
