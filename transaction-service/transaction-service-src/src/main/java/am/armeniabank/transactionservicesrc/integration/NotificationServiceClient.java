package am.armeniabank.transactionservicesrc.integration;

import am.armeniabank.transactionserviceapi.enums.Currency;
import am.armeniabank.transactionserviceapi.enums.NotificationType;
import am.armeniabank.transactionserviceapi.request.NotificationRequest;
import am.armeniabank.transactionserviceapi.response.NotificationResponse;
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
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceClient {

    private final RestTemplate restTemplate;

    @Value("${notification-service.url}")
    private String notificationServiceUrl;

    public void sendNotification(UUID userId,
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
    public void sendTransactionNotification(UUID senderId,
                                            UUID receiverId,
                                            BigDecimal amount,
                                            Currency currency,
                                            String senderMessage,
                                            String receiverMessage,
                                            String token) {
        sendNotification(senderId, amount, currency, "Transaction Update", senderMessage, token);

        sendNotification(receiverId, amount, currency, "Transaction Update", receiverMessage, token);
    }
}