package co.com.crediya.requests.api.dto;

import java.math.BigDecimal;

public record LoanApplicationResponse(
        Integer id,
        BigDecimal amount,
        int term,
        String email,
        String status,
        String typeLoan
) {
}
