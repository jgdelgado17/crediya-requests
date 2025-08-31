package co.com.crediya.requests.model.status;

import lombok.Getter;

@Getter
public enum StatusEnum {
    PENDING_REVIEW("PENDING_REVIEW"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String value;

    StatusEnum(String value) {
        this.value = value;
    }
}
