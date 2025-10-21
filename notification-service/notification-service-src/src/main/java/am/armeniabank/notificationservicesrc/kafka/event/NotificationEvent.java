package am.armeniabank.notificationservicesrc.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private UUID senderId;
    private UUID receiverId;
    private String senderName;
    private String receiverName;
    private BigDecimal amount;
    private BigDecimal senderBalanceAfter;
    private BigDecimal receiverBalanceAfter;
    private String currency;
}
