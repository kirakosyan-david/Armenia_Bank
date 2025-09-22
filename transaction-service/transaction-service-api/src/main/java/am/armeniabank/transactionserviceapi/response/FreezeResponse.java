package am.armeniabank.transactionserviceapi.response;

import am.armeniabank.transactionserviceapi.enums.FreezeStatus;
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
public class FreezeResponse {

    private UUID id;
    private UUID walletId;
    private BigDecimal amount;
    private String reason;
    private FreezeStatus freezeStatus;
    private LocalDateTime createdAt;
    private LocalDateTime releasedAt;
    private TransactionResponse transaction;
}
