package am.armeniabank.transactionservicesrc.integration;


import am.armeniabank.transactionserviceapi.request.AuditTransactionEventRequest;
import am.armeniabank.transactionserviceapi.response.UserResponse;
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

    public void sendAuditTransactionEvent(UUID transactionId, UUID fromWalletId, UUID toWalletId,
                                          UserResponse user, String action) {
        if (user == null || user.getId() == null) {
            log.warn("User is null or userId is missing, skipping audit event");
            return;
        }

        String message = String.format("[%s] TransactionId=%s UserId=%s User=%s %s",
                action, transactionId, user.getId(), user.getFirstName(), user.getLastName());

        System.out.println("messages: " + message);
        AuditTransactionEventRequest request = AuditTransactionEventRequest.builder()
                .service("Transaction-Service")
                .transactionId(transactionId)
                .fromWalletId(fromWalletId) // <- нужно добавить
                .toWalletId(toWalletId)     // <- нужно добавить
                .eventType(action)
                .userId(user.getId())
                .details(message)
                .createdAt(LocalDateTime.now())
                .build();
        log.debug("Sending audit request: {}", request);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AuditTransactionEventRequest> httpEntity = new HttpEntity<>(request, headers);

            ResponseEntity<Void> response = restTemplate.postForEntity(
                    auditServiceUrl + "/api/audit/transaction",
                    httpEntity,
                    Void.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Audit transaction event sent successfully: {}", message);
            } else {
                log.warn("Audit transaction event for transactionId={} userId={} sent but received status: {}",
                        transactionId, user.getId(), response.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("Error sending audit transaction event for transactionId={} userId={}: {}",
                    transactionId, user.getId(), e.getMessage(), e);
        }
    }
}