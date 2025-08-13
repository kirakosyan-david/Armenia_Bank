package am.armeniabank.authservicesrc.integration;

import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
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
public class AuditServiceClient {

    private final RestTemplate restTemplate;
    private final String auditServiceUrl;

    public AuditServiceClient(@Value("${audit-service.url}") String auditServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.auditServiceUrl = auditServiceUrl;
    }

    public void sendAuditEvent(AuditEvent event) {
        String url = auditServiceUrl + "/api/audit";
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
        }
    }
}
