package am.armeniabank.transactionservicesrc.kafka.model;

import am.armeniabank.armeniabankcommon.enums.Currency;
import am.armeniabank.transactionserviceapi.enums.TransactionType;
import am.armeniabank.transactionserviceapi.enums.TransactionState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {

    private UUID transactionId;
    private UUID fromWalletId;
    private UUID toWalletId;
    private UUID userId;
    private BigDecimal amount;
    private Currency currency;
    private TransactionType type;
    private TransactionState status;
    private LocalDateTime timestamp;
    private String reason;
}
