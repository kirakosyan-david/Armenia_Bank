package am.armeniabank.authservicesrc.kafka.producer.impl;

import am.armeniabank.authservicesrc.kafka.model.UserEvent;
import am.armeniabank.authservicesrc.kafka.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer implements EventProducer<UserEvent> {

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.auth-events}")
    private String topic;

    @Override
    public void handle(UserEvent event) {
        log.info("Sending an event to Kafka: {}", event);

       kafkaTemplate.send(topic, event).thenAccept(result ->{
           RecordMetadata recordMetadata = result.getRecordMetadata();
           log.info("Message successfully sent to Kafka, topic: {}, partition: {}, offset: {}",
                   recordMetadata.topic(),
                   recordMetadata.partition(),
                   recordMetadata.offset());
       }).exceptionally(ex -> {
           log.error("Error sending message to Kafka ", ex);
           return null;
       });

    }
}
