package am.armeniabank.auditserviceapi.response;

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
public class AuditTransactionEventResponse {

    private String service;
    private UUID fromWalletId;
    private UUID toWalletId;
    private String eventType;
    private UUID userId;
    private String details;
    private LocalDateTime createdAt;
    private String firstName;
    private String lastName;
}