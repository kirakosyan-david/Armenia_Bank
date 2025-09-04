package am.armeniabank.walletserviceapi.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditWalletEventRequest {

    private String service;
    private UUID walletId;
    private String eventType;
    private UUID userId;
    private String details;
    private LocalDateTime createdAt;
}
