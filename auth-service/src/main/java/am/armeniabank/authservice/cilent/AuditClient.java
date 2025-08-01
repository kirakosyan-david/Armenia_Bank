package am.armeniabank.authservice.cilent;

import am.armeniabank.authservice.dto.AuditEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class AuditClient {

    private final RestTemplate restTemplate;
    private final String auditServiceUrl;

    public AuditClient(@Value("${audit-service.url}") String auditServiceUrl) {
        this.restTemplate = new RestTemplate();
        this.auditServiceUrl = auditServiceUrl;
    }

    public void sendAuditEvent(AuditEventDto event) {
        String url = auditServiceUrl + "/api/audit";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AuditEventDto> request = new HttpEntity<>(event, headers);

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
