package co.com.crediya.requests.usecase.status;

import co.com.crediya.requests.model.shared.enums.EnumUtils;
import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.status.StatusEnum;
import reactor.core.publisher.Mono;

public class StatusValidator {

    /**
     * Validate a status.
     *
     * <p>This method checks if the status name is valid. If the name is null or empty, a
     * {@link Mono} that emits an error is returned. If the name is valid, then a
     * {@link Mono} that emits the status is returned. If the name is invalid, a
     * {@link Mono} that emits an error is returned.
     *
     * @param status the status to be validated. The status cannot be null.
     * @return a {@link Mono} that emits a valid status or an error.
     */
    public static Mono<Status> validate(Status status) {
        if (status.getNames() == null || status.getNames().isEmpty()){
            return Mono.error(new IllegalArgumentException(ErrorMessages.requiredField("status name")));
        }

        try {
            EnumUtils.fromString(StatusEnum.class, status.getNames());
            return Mono.just(status);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }
    }

    /**
     * Validate a status name.
     *
     * <p>This method checks if the status name is valid. If the name is null or empty, a
     * {@link Mono} that emits an error is returned. If the name is valid, then a
     * {@link Mono} that emits the name is returned. If the name is invalid, a
     * {@link Mono} that emits an error is returned.
     *
     * @param name the name to be validated. The name cannot be null.
     * @return a {@link Mono} that emits a valid name or an error.
     */
    public static Mono<String> validateName(String name) {
        if (name == null || name.isEmpty()){
            return Mono.error(new IllegalArgumentException(ErrorMessages.requiredField("status name")));
        }
        return Mono.just(name);
    }
}
