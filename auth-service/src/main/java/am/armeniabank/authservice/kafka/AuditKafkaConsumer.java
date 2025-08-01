package am.armeniabank.authservice.kafka;

import am.armeniabank.authservice.dto.AuditEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditKafkaConsumer {

    @KafkaListener(topics = "${kafka.topic.auth-events}", groupId = "auth-service")
    public void consumeAuditEvent(AuditEventDto event) {
        log.info("Получено audit-событие: {}", event);
    }
}