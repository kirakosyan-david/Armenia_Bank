package am.armeniabank.auditservice.kafka;

import am.armeniabank.auditservice.dto.AuditEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditKafkaConsumer {

    @KafkaListener(topics = "${kafka.topic.audit-events}", groupId = "audit-service")
    public void consumeAuditEvent(AuditEventDto event) {
        log.info("Получено audit-событие: {}", event);
        // сохранить в базу, логировать и т.п.
    }
}
