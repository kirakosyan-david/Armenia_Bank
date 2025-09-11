package am.armeniabank.walletservicesrc.kafka.consumer.impl;

import am.armeniabank.walletservicesrc.kafka.consumer.EventConsumer;
import am.armeniabank.walletservicesrc.kafka.model.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditEventConsumer implements EventConsumer<AuditEvent> {

    @Override
    @KafkaListener(topics = "${spring.kafka.topic.audit-events}",
            groupId = "wallet-service", containerFactory = "auditEventKafkaListenerFactory")
    public void handle(AuditEvent event) {
        log.info("Audit event received: {}", event);
    }

}