package co.com.crediya.requests.model.securityports;

import co.com.crediya.requests.model.shared.enums.FromStringEnum;
import lombok.Getter;

@Getter
public enum RoleEnum implements FromStringEnum<String> {
    ADMIN("ADMIN"),
    ADVISOR("ADVISOR"),
    CLIENT("CLIENT");

    private String value;

    RoleEnum(String value) {
        this.value = value;
    }

}
