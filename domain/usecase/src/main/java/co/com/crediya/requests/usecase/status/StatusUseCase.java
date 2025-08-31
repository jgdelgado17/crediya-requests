package co.com.crediya.requests.usecase.status;

import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.status.gateways.StatusRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class StatusUseCase {
    private final StatusRepository statusRepository;

    /**
     * Save a new status.
     *
     * @param status the status to be saved. The {@link Status#names} field
     *               cannot be empty.
     * @return a {@link Mono} that emits a validated {@link Status} or an
     * error if the {@link Status#names} field is empty.
     * @see StatusValidator#validate(Status)
     */
    public Mono<Status> save(Status status) {
        return StatusValidator.validate(status)
                .flatMap(statusRepository::save);
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
