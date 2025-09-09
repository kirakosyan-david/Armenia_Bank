package am.armeniabank.walletservicesrc.integration;

import am.armeniabank.walletserviceapi.request.AuditWalletEventRequest;
import am.armeniabank.walletserviceapi.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditServiceClient {

    private final RestTemplate restTemplate;

    @Value("${audit-service.url}")
    private String auditServiceUrl;

    public void sendAuditWalletEvent(UUID walletId, UserResponse user, String action) {
        if (user == null || user.getId() == null) {
            log.warn("User is null or userId is missing, skipping audit event");
            return;
        }

        String message = String.format("[%s] WalletId=%s UserId=%s User=%s %s",
                action, walletId, user.getId(), user.getFirstName(), user.getLastName());

        AuditWalletEventRequest request = AuditWalletEventRequest.builder()
                .service("Wallet-Service")
                .walletId(walletId)
                .eventType(action)
                .userId(user.getId())
                .details(message)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AuditWalletEventRequest> httpEntity = new HttpEntity<>(request, headers);

            ResponseEntity<Void> response = restTemplate.postForEntity(
                    auditServiceUrl + "/api/audit/wallet",
                    httpEntity,
                    Void.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Audit wallet event sent successfully: {}", message);
            } else {
                log.warn("Audit wallet event for walletId={} userId={} sent but received status: {}",
                        walletId, user.getId(), response.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("Error sending audit wallet event for walletId={} userId={}: {}",
                    walletId, user.getId(), e.getMessage(), e);
        }
    }
}