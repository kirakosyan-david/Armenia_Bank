package am.armeniabank.authservicesrc.integration;

import am.armeniabank.armeniabankcommon.constants.ApiConstants;
import am.armeniabank.authservicesrc.exception.custom.UserServerError;
import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
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

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditServiceClient {

    private final RestTemplate restTemplate;

    @Value("${audit-service.url}")
    private String auditServiceUrl;

    public void sendAuditEvent(AuditEvent event) {
        String url = auditServiceUrl + ApiConstants.AUDIT_USER_SERVICE_URL;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AuditEvent> request = new HttpEntity<>(event, headers);

            ResponseEntity<Void> response = restTemplate.postForEntity(url, request, Void.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Audit event sent successfully");
            } else {
                log.warn("Audit event sent but status received: {}", response.getStatusCode());
            }

        } catch (RestClientException e) {
            log.error("Error sending audit event: {}", e.getMessage(), e);
            throw new UserServerError("Failed to send audit event", e);
        }
    }
}
