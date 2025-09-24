package co.com.crediya.requests.model.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder(toBuilder = true)
public class NotificationRequest {
    private String applicantEmail;
    private String applicantName;
    private String status;
    private BigDecimal loanAmount;
}
