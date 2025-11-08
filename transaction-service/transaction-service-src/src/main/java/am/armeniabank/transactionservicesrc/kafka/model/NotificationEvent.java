package am.armeniabank.transactionservicesrc.kafka.model;

import am.armeniabank.armeniabankcommon.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
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
    private Currency currency;
    private String message;
}
