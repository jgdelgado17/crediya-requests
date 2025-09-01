package co.com.crediya.requests.r2dbc.modules.requests.mapper;

import co.com.crediya.requests.model.requests.Requests;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.typeloan.TypeLoan;
import co.com.crediya.requests.r2dbc.modules.requests.data.RequestsEntity;
import org.springframework.stereotype.Component;

@Component
public class RequestsMapper {
    /**
     * Converts a {@link Requests} to a {@link RequestsEntity}.
     *
     * @param requests The request to be converted.
     * @return The converted request entity.
     */
    public RequestsEntity toRequestsEntity(Requests requests){
        return RequestsEntity.builder()
                .id(requests.getId())
                .amount(requests.getAmount())
                .term(requests.getTerm())
                .email(requests.getEmail())
                .typeLoan(requests.getTypeLoan().getId())
                .status(requests.getStatus().getId())
                .build();
    }

    /**
     * Converts a {@link RequestsEntity} to a {@link Requests}.
     *
     * <p>This method requires the status and type loan of the request to be provided.
     *
     * @param requestsEntity The request entity to be converted.
     * @param status The status of the request.
     * @param typeLoan The type loan of the request.
     * @return The converted request.
     */
    public Requests toRequests(RequestsEntity requestsEntity, Status status, TypeLoan typeLoan){
        return Requests.builder()
                .id(requestsEntity.getId())
                .amount(requestsEntity.getAmount())
                .term(requestsEntity.getTerm())
                .email(requestsEntity.getEmail())
                .typeLoan(typeLoan)
                .status(status)
                .build();
    }
}
