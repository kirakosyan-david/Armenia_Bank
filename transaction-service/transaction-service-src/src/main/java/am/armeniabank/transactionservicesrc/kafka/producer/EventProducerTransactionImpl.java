package am.armeniabank.transactionservicesrc.kafka.producer;

import am.armeniabank.armeniabankcommon.kafka.producer.EventProducer;
import am.armeniabank.transactionservicesrc.kafka.model.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventProducerTransactionImpl implements EventProducer<TransactionEvent> {

    private final KafkaTemplate<String, TransactionEvent> treansactionKafkaTemplate;

    @Value("${spring.kafka.topic.transaction-events}")
    private String transactionTopic;

    @Override
    public void handle(TransactionEvent event) {
        treansactionKafkaTemplate.send(transactionTopic, event.getTransactionId().toString(), event);
        log.info("Sent TransactionEvent: {}", event);
    }
}
