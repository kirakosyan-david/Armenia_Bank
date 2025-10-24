package am.armeniabank.transactionservicesrc.kafka.producer.impl;

import am.armeniabank.transactionservicesrc.kafka.model.FreezeEvent;
import am.armeniabank.transactionservicesrc.kafka.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventProducerFreezeImpl implements EventProducer<FreezeEvent> {

    private final KafkaTemplate<String, FreezeEvent> freezeKafkaTemplate;

    @Value("${spring.kafka.topic.freeze-events}")
    private String freezeTopic;

    @Override
    public void handle(FreezeEvent event) {
        freezeKafkaTemplate.send(freezeTopic, event.getFreezeId().toString(), event);
        log.info("Sent FreezeEvent: {}", event);
    }
}
