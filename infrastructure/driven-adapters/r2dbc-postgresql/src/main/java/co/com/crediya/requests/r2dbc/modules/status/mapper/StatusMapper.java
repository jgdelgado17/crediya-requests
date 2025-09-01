package co.com.crediya.requests.r2dbc.modules.status.mapper;

import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.r2dbc.modules.status.data.StatusEntity;
import org.springframework.stereotype.Component;

@Component
public class StatusMapper {
    public StatusEntity toEntity(Status status){
        return StatusEntity.builder()
                .id(status.getId())
                .names(status.getNames())
                .description(status.getDescription())
                .build();
    }

    public Status toModel(StatusEntity statusEntity){
        return Status.builder()
                .id(statusEntity.getId())
                .names(statusEntity.getNames())
                .description(statusEntity.getDescription())
                .build();
    }
}
