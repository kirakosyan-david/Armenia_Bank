package am.armeniabank.notificationservicesrc.kafka.listener;

import am.armeniabank.notificationservicesrc.entity.Notification;
import am.armeniabank.notificationservicesrc.kafka.event.NotificationEvent;
import am.armeniabank.notificationservicesrc.mapper.NotificationMapper;
import am.armeniabank.notificationservicesrc.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @KafkaListener(topics = "notification-events-topic", groupId = "my-group")
    public void handleTransactionEvent(NotificationEvent event) {

        Notification senderNotification = notificationMapper.toSenderNotification(event);
        notificationService.sendNotification(senderNotification);


        Notification receiverNotification = notificationMapper.toReceiverNotification(event);
        notificationService.sendNotification(receiverNotification);
    }
}
