package am.armeniabank.authservicesrc.kafka.producer.impl;

import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
import am.armeniabank.authservicesrc.kafka.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventProducer implements EventProducer<AuditEvent> {

    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.audit-events}")
    private String topic;

    @Override
    public void handle(AuditEvent event) {
        log.info("Sending audit event to Kafka: {}", event);
        kafkaTemplate.send(topic, event);
    }
}
