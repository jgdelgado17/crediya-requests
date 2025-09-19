package co.com.crediya.requests.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusLoanApplicationRequest(
        @NotBlank(message = "Status name is required")
        String statusName,
        @NotNull(message = "Id loan application is required")
        Integer idLoanApplication
) {
}
