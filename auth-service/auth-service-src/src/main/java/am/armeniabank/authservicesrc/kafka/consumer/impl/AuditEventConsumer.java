package am.armeniabank.authservicesrc.kafka.consumer.impl;

import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
import am.armeniabank.authservicesrc.kafka.consumer.EventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditEventConsumer implements EventConsumer<AuditEvent> {

    @Override
    @KafkaListener(topics = "${spring.kafka.topic.audit-events}",
            groupId = "auth-service", containerFactory = "auditEventKafkaListenerFactory")
    public void handle(AuditEvent event) {
        log.info("Audit event received: {}", event);
    }
}