package am.armeniabank.auditservicesrc.kafka.consumer.impl;

import am.armeniabank.auditservicesrc.kafka.model.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditKafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.topic.audit-events}", groupId = "audit-service")
    public void consumeAuditEvent(AuditEvent event) {
        log.info("Audit event received: {}", event);
    }
}
