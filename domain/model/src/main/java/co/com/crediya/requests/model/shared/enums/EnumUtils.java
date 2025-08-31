package co.com.crediya.requests.model.shared.enums;

import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;

public class EnumUtils {
    public static <E extends Enum<E> & FromStringEnum<String>> E fromString(
            Class<E> enumClass, String value) {

        for (E e : enumClass.getEnumConstants()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(ErrorMessages.invalidEnumValue(enumClass.getSimpleName(), value));
    }
}
