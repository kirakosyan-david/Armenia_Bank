package am.armeniabank.walletserviceapi.response;

import am.armeniabank.armeniabankcommon.enums.Currency;
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
public class WalletResponse {

    private UUID id;

    private UUID userId;

    private String firstName;

    private String lastName;

    private Currency currency;

    private BigDecimal balance;

    private WalletStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
