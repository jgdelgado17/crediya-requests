package co.com.crediya.requests.r2dbc.modules.status.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
public class StatusEntity {
    @Id
    private Integer id;
    private String names;
    private String description;
}
