package am.armeniabank.notificationserviceapi.request;

import am.armeniabank.armeniabankcommon.enums.Currency;
import am.armeniabank.armeniabankcommon.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private UUID userId;

    private String title;

    private String message;

    private BigDecimal amount;

    private BigDecimal balanceAfter;

    private NotificationType type;
    private Currency currency;

    private LocalDateTime createdAt;
}