package am.armeniabank.walletservicesrc.integration;

import am.armeniabank.walletserviceapi.enums.NotificationType;
import am.armeniabank.walletserviceapi.request.AuditWalletEventRequest;
import am.armeniabank.walletserviceapi.request.NotificationRequest;
import am.armeniabank.walletserviceapi.response.NotificationResponse;
import am.armeniabank.walletserviceapi.response.UserResponse;
import am.armeniabank.walletservicesrc.entity.Wallet;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceClient {

    private final RestTemplate restTemplate;

    @Value("${notification-service.url}")
    private String notificationServiceUrl;

    public void sendNotificationWalletEvent(UUID userId,
                                            BigDecimal amount,
                                            Currency currency,
                                            String title,
                                            String message,
                                            String token) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            NotificationRequest request = NotificationRequest.builder()
                    .userId(userId)
                    .amount(amount)
                    .currency(currency)
                    .title(title)
                    .message(message)
                    .type(NotificationType.TRANSACTIONAL)
                    .createdAt(LocalDateTime.now())
                    .build();

            HttpEntity<NotificationRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<NotificationResponse> response = restTemplate.postForEntity(
                    notificationServiceUrl + "/api/notifications",
                    entity,
                    NotificationResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Notification sent successfully to userId={} title='{}'", userId, title);
            } else {
                log.warn("Notification request for userId={} returned status: {}", userId, response.getStatusCode());
            }

        } catch (RestClientException e) {
            log.error("Error sending notification to userId={}: {}", userId, e.getMessage(), e);
        }
    }
}