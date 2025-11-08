package am.armeniabank.auditservicesrc.kafka.consumer;

import am.armeniabank.armeniabankcommon.kafka.consumer.EventConsumer;
import am.armeniabank.auditservicesrc.kafka.model.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditKafkaConsumer implements EventConsumer<AuditEvent> {

    @KafkaListener(topics = "${spring.kafka.topic.audit-events}", groupId = "audit-service")@Override
    public void handle(AuditEvent event) {
        log.info("Audit event received: {}", event);
    }
}
