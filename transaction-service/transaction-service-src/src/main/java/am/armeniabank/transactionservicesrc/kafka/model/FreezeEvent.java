package am.armeniabank.transactionservicesrc.kafka.model;

import am.armeniabank.transactionserviceapi.enums.FreezeStatus;
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
public class FreezeEvent {

    private UUID freezeId;
    private UUID walletId;
    private UUID transactionId;
    private BigDecimal amount;
    private FreezeStatus freezeStatus;
    private LocalDateTime createdAt;
    private String reason;
}
