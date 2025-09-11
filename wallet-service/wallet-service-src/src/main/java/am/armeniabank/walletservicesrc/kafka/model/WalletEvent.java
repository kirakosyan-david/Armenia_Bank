package am.armeniabank.walletservicesrc.kafka.model;

import am.armeniabank.walletserviceapi.request.WalletOperationRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletEvent {

    private UUID walletId;
    private WalletOperationRequest request;
}
