package am.armeniabank.walletserviceapi.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Service name must not be blank")
    @Size(max = 200, message = "Service name must not exceed 200 characters")
    private String service;

    @NotNull(message = "Wallet ID must not be null")
    private UUID walletId;

    @NotBlank(message = "Event type must not be blank")
    @Size(max = 50, message = "Event type must not exceed 50 characters")
    private String eventType;

    @NotNull(message = "User ID must not be null")
    private UUID userId;

    @Size(max = 500, message = "Details must not exceed 500 characters")
    private String details;

    @NotNull(message = "CreatedAt must not be null")
    @PastOrPresent(message = "CreatedAt must be in the past or present")
    private LocalDateTime createdAt;
}
