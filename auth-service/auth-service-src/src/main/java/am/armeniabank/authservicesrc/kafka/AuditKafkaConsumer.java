package am.armeniabank.authservicesrc.kafka;

import am.armeniabank.authserviceapi.response.AuditEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditKafkaConsumer {

    @KafkaListener(topics = "${kafka.topic.auth-events}", groupId = "auth-service")
    public void consumeAuditEvent(AuditEventResponse event) {
        log.info("Получено audit-событие: {}", event);
    }
}