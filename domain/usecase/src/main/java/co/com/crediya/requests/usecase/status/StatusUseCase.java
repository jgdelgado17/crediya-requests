package co.com.crediya.requests.usecase.status;

import co.com.crediya.requests.model.shared.exceptions.ErrorMessages;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.status.gateways.StatusRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class StatusUseCase {
    private final StatusRepository statusRepository;

    /**
     * Saves a status.
     *
     * <p>This method validates the status and if it is valid, it saves the status in the database.
     * If the status name already exists, a {@link Mono} that emits an error is returned.
     *
     * @param status the status to be saved. The status cannot be null.
     * @return a {@link Mono} that emits the saved status or an error if the status name is already taken.
     */
    public Mono<Status> save(Status status) {
        return StatusValidator.validate(status)
                .flatMap(validStatus ->
                        statusRepository.findByName(validStatus.getNames())
                                .flatMap(existingStatus ->
                                        Mono.error(new IllegalArgumentException(ErrorMessages.objectAlreadyExists(validStatus.getNames()))).cast(Status.class)
                                )
                                .switchIfEmpty(Mono.defer(() -> statusRepository.save(validStatus)))
                );

    }

    /**
     * Finds a status by name.
     *
     * @param name the name of the status to be found. The name cannot be
     *             empty.
     * @return a {@link Mono} that emits the found status or an error if
     * the name is empty.
     * @see StatusValidator#validateName(String)
     */
    public Mono<Status> findByName(String name) {
        return StatusValidator.validateName(name)
                .flatMap(statusRepository::findByName);
    }
}
