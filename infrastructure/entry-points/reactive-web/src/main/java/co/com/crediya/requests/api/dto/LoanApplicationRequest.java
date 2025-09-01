package co.com.crediya.requests.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanApplicationRequest(
        @NotNull(message = "Amount is required")
        BigDecimal amount,
        @NotNull(message = "Term is required")
        int term,
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "TypeLoan is required")
        String typeLoan
) {
}
