package am.armeniabank.walletserviceapi.request;

import am.armeniabank.walletserviceapi.enums.WalletOperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletOperationRequest {

    private WalletOperationType walletOperationType;

    private BigDecimal amount;
}
