package co.com.crediya.requests.api.mapper;

import co.com.crediya.requests.api.dto.StatusRequest;
import co.com.crediya.requests.model.status.Status;

public class StatusDataMapper {
    public static Status toStatus(StatusRequest statusRequest){
        return Status.builder()
                .names(statusRequest.name())
                .description(statusRequest.description())
                .build();
    }
}
