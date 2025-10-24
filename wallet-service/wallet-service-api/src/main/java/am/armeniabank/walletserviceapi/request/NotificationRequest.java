package am.armeniabank.walletserviceapi.request;

import am.armeniabank.walletserviceapi.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    private UUID userId;
    private BigDecimal amount;
    private Currency currency;
    private String title;
    private String message;
    private NotificationType type;
    private LocalDateTime createdAt;
}
