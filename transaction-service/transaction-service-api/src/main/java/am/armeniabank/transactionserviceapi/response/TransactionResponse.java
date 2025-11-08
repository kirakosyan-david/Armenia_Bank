package am.armeniabank.transactionserviceapi.response;

import am.armeniabank.armeniabankcommon.enums.Currency;
import am.armeniabank.transactionserviceapi.enums.TransactionState;
import am.armeniabank.transactionserviceapi.enums.TransactionType;
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
public class TransactionResponse {


    private UUID id;
    private UUID fromWalletId;
    private UUID toWalletId;
    private UUID userId;
    private BigDecimal amount;
    private Currency currency;
    private TransactionType type;
    private TransactionState status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
