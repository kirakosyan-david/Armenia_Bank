package am.armeniabank.transactionserviceapi.response;

import am.armeniabank.transactionserviceapi.enums.Currency;
import am.armeniabank.transactionserviceapi.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private String title;

    private String message;

    private BigDecimal amount;

    private BigDecimal balanceAfter;

    private NotificationType type;

    private Currency currency;

    private LocalDateTime createdAt;
}