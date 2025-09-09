package co.com.crediya.requests.model.loanApplication;
import co.com.crediya.requests.model.status.Status;
import co.com.crediya.requests.model.typeloan.TypeLoan;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {
    private Integer id;
    private BigDecimal amount;
    private int term;
    private String email;
    private Status status;
    private TypeLoan typeLoan;
}
