package am.armeniabank.transactionserviceapi.response;

import am.armeniabank.transactionserviceapi.enums.Currency;
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
public class WalletResponse {

    private UUID walletId;
    private UUID userId;
    private BigDecimal balance;
    private BigDecimal frozenBalance;
    private Currency currency;
}
