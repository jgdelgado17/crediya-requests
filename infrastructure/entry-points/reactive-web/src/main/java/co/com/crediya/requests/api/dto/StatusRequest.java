package co.com.crediya.requests.api.dto;

import jakarta.validation.constraints.NotBlank;

public record StatusRequest(
        @NotBlank(message = "Name is required")
        String name,
        String description
) {
}
