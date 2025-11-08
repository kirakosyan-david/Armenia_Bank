package am.armeniabank.transactionservicesrc.kafka.consumer;

import am.armeniabank.armeniabankcommon.kafka.consumer.EventConsumer;
import am.armeniabank.transactionservicesrc.kafka.model.NotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationEventConsumer implements EventConsumer<NotificationEvent> {

    @Override
    @KafkaListener(topics = "notification-events", groupId = "notification-service-group")
    public void handle(NotificationEvent event) {
        log.info("Received notification: {} -> {}, message: {}",
                event.getSenderId(), event.getReceiverId(), event.getMessage());
    }

}
