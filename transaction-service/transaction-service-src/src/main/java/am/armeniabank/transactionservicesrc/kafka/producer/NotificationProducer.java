package am.armeniabank.transactionservicesrc.kafka.producer;


import am.armeniabank.armeniabankcommon.kafka.producer.EventProducer;
import am.armeniabank.transactionservicesrc.kafka.model.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer implements EventProducer<NotificationEvent> {

    private final KafkaTemplate<String, NotificationEvent> notificationKafkaTemplate;

    @Value("${spring.kafka.topic.notification-events}")
    private String topic;

    @Override
    public void handle(NotificationEvent event) {
        log.info("Sending NotificationEvent to Kafka: {}", event);
        notificationKafkaTemplate.send(topic, event);
    }
}
