package co.com.crediya.requests.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TypeLoanRequest(
        @NotBlank(message = "Name is required")
        String Name,
        @NotNull(message = "Min amount is required")
        BigDecimal minAmount,
        @NotNull(message = "Max amount is required")
        BigDecimal maxAmount,
        @NotNull(message = "Interest rate is required")
        BigDecimal interestRate,
        Boolean automaticValidation
) {
}
