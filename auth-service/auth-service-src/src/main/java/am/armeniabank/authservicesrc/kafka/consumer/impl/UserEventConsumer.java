package am.armeniabank.authservicesrc.kafka.consumer.impl;

import am.armeniabank.authservicesrc.handler.UserEventHandler;
import am.armeniabank.authservicesrc.kafka.consumer.EventConsumer;
import am.armeniabank.authservicesrc.kafka.model.UserEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class UserEventConsumer implements EventConsumer<UserEvent> {

    private final List<UserEventHandler> handlers;

    @Override
    @KafkaListener(topics = "${spring.kafka.topic.auth-events}",
            containerFactory = "userEventKafkaListenerFactory")
    public void handle(UserEvent event) {
        handlers.stream()
                .filter(handler -> handler.isHandle(event))
                .forEach(hendler -> {
                    try {
                        hendler.handle(event);
                    } catch (Exception e) {
                        log.error("Error handling UserEvent", e);
                    }
                });
    }

}
