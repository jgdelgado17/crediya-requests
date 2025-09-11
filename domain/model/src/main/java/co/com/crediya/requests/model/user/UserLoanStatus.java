package co.com.crediya.requests.model.user;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserLoanStatus {
    private String name;
    private String email;
    private String documentNumber;
    private Float baseSalary;
    private Float totalMonthlyDebt;
    private String loanStatus;
    private String loanType;
    private BigDecimal loanAmount;
    private int loanTerm;
    private BigDecimal loanInterestRate;
}
