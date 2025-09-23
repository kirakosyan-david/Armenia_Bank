package am.armeniabank.transactionservicesrc.kafka.consumer.impl;

import am.armeniabank.transactionservicesrc.kafka.consumer.EventConsumer;
import am.armeniabank.transactionservicesrc.kafka.model.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditEventConsumer implements EventConsumer<AuditEvent> {

    @Override
    @KafkaListener(
            topics = "audit-events",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(AuditEvent event) {
        log.info("Audit event received: {}", event);
    }

}