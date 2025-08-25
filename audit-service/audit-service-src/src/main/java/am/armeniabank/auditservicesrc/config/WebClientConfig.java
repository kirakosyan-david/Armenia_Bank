package am.armeniabank.auditservicesrc.config;

import am.armeniabank.auditservicesrc.entity.AuditUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final RestTemplate restTemplate;

    @Value("${audit-service.base-url}")
    private String auditServiceBaseUrl;

    public void sendAuditEvent(AuditUser event) {
        String url = auditServiceBaseUrl + "/audit/events";
        restTemplate.postForEntity(url, event, Void.class);
    }
}
