package co.com.crediya.requests.model.status;

import co.com.crediya.requests.model.shared.enums.FromStringEnum;
import lombok.Getter;

@Getter
public enum StatusEnum implements FromStringEnum<String> {
    PENDING_REVIEW("PENDING_REVIEW"),
    MANUAL_REVIEW("MANUAL_REVIEW"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String value;

    StatusEnum(String value) {
        this.value = value;
    }
}
