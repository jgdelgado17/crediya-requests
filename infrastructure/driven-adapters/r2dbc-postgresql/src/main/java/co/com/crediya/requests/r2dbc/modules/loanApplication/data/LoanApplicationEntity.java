package co.com.crediya.requests.r2dbc.modules.loanApplication.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class LoanApplicationEntity {
    @Id
    private Integer id;
    private BigDecimal amount;
    private int term;
    private String email;
    private Integer status;
    @Column("type_loan")
    private Integer typeLoan;
}
