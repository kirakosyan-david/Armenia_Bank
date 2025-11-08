package am.armeniabank.walletserviceapi.response;

import am.armeniabank.armeniabankcommon.enums.Currency;
import am.armeniabank.armeniabankcommon.enums.WalletOperationType;
import am.armeniabank.walletserviceapi.enums.WalletOperationReason;
import am.armeniabank.walletserviceapi.enums.WalletStatus;
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
public class WalletOperationResponse {

    private UUID id;

    private WalletOperationType walletOperationType;

    private BigDecimal amount;

    private WalletOperationReason walletOperationReason;

    private LocalDateTime createdAt;

    private UUID userId;

    private Currency currency;

    private BigDecimal balance;

    private WalletStatus status;
}
