package co.com.crediya.requests.api.mapper;

import co.com.crediya.requests.api.dto.TypeLoanRequest;
import co.com.crediya.requests.model.typeloan.TypeLoan;

public class TypeLoanDataMapper {
    public static TypeLoan toTypeLoan(TypeLoanRequest typeLoanRequest){
        return TypeLoan.builder()
                .names(typeLoanRequest.name())
                .interestRate(typeLoanRequest.interestRate())
                .minAmount(typeLoanRequest.minAmount())
                .maxAmount(typeLoanRequest.maxAmount())
                .automaticValidation(typeLoanRequest.automaticValidation())
                .build();
    }
}
