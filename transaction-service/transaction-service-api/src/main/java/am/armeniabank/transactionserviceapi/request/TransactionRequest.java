package am.armeniabank.transactionserviceapi.request;

import am.armeniabank.transactionserviceapi.enums.Currency;
import am.armeniabank.transactionserviceapi.enums.TransactionType;
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
public class TransactionRequest {

    private UUID fromWalletId;
    private UUID toWalletId;
    private UUID userId;
    private BigDecimal amount;
    private Currency currency;
    private TransactionType type;
}
