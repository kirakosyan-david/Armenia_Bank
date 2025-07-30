package am.armeniabank.authservice.cilent;

import am.armeniabank.authservice.dto.AuditEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@Slf4j
public class AuditClient {

    private final WebClient webClient;

    public AuditClient(@Value("${audit-service.url}") String auditServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(auditServiceUrl)
                .build();
    }

    public Mono<Void> sendAuditEvent(AuditEventDto event) {
        return webClient.post()
                .uri("/api/audit")
                .bodyValue(event)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .filter(throwable -> throwable instanceof WebClientRequestException)
                        .doBeforeRetry(retrySignal -> log.warn("Повторная попытка отправки события аудита, попытка {}", retrySignal.totalRetries() + 1))
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new RuntimeException("Не удалось отправить событие аудита после повторных попыток", retrySignal.failure())))
                .doOnSuccess(v -> log.info("Событие аудита успешно отправлено"))
                .doOnError(e -> log.error("Ошибка при отправке события аудита: {}", e.getMessage(), e));
    }
}
