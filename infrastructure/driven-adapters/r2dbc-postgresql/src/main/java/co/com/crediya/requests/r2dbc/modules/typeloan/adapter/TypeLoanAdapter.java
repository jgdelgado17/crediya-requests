package co.com.crediya.requests.r2dbc.modules.typeloan.adapter;

import co.com.crediya.requests.model.typeloan.TypeLoan;
import co.com.crediya.requests.model.typeloan.gateways.TypeLoanRepository;
import co.com.crediya.requests.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.requests.r2dbc.modules.typeloan.data.TypeLoanEntity;
import co.com.crediya.requests.r2dbc.modules.typeloan.repository.TypeLoanRepositoryCrud;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TypeLoanAdapter
    extends ReactiveAdapterOperations<TypeLoan, TypeLoanEntity, Integer, TypeLoanRepositoryCrud>
    implements TypeLoanRepository {

    private static final Logger log = LoggerFactory.getLogger(TypeLoanAdapter.class);

    protected TypeLoanAdapter(TypeLoanRepositoryCrud repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, TypeLoan.class));
        this.repository = repository;
    }

    /**
     * Saves a type loan.
     *
     * <p>This method logs the beginning and the end of the type loan saving process.
     * If the type loan is saved successfully, a {@link Mono} that emits the saved type loan
     * is returned. If the type loan cannot be saved, a {@link Mono} that emits an error is returned.
     *
     * @param typeLoan the type loan to be saved. The type loan cannot be null.
     * @return a {@link Mono} that emits the saved type loan or an error.
     */
    @Override
    public Mono<TypeLoan> save(TypeLoan typeLoan) {
        log.info("Saving type loan: {}", typeLoan.getName());
        return super.save(typeLoan)
                .doOnSuccess(typeLoanSaved -> log.info("Type loan saved: {}", typeLoanSaved.getName()))
                .doOnError(error -> log.error("Error saving type loan: {}", typeLoan.getName(), error))
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
    }

    /**
     * Finds a type loan by name.
     *
     * <p>This method logs the beginning of the type loan finding process.
     * If the type loan is found successfully, a {@link Mono} that emits the found type loan
     * is returned. If the type loan cannot be found, a {@link Mono} that emits an empty value is returned.
     * If an error occurs during the finding process, a {@link Mono} that emits an error is returned.
     *
     * @param name the name of the type loan to be found. The name cannot be null or empty.
     * @return a {@link Mono} that emits the found type loan or an error.
     */
    @Override
    public Mono<TypeLoan> findByName(String name) {
        log.info("Finding type loan by name: {}", name);
        return repository.findByNames(name)
                .map(super::toEntity)
                .doOnSuccess(typeLoan -> {
                    if (typeLoan == null) {
                        log.info("Type loan not found: {}", name);
                    } else {
                        log.info("Type loan found: {}", typeLoan.getName());
                    }
                })
                .doOnError(error -> log.error("Error finding type loan: {}", error.getMessage()))
                .onErrorResume(error -> Mono.error(new RuntimeException(error.getMessage())));
    }
}
