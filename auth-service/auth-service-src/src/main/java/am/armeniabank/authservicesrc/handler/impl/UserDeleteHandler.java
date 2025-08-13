package am.armeniabank.authservicesrc.handler.impl;

import am.armeniabank.authservicesrc.handler.UserEventHandler;
import am.armeniabank.authservicesrc.kafka.model.enumeration.UserEventType;
import am.armeniabank.authservicesrc.kafka.model.UserEvent;
import am.armeniabank.authservicesrc.repository.UserRepository;
import am.armeniabank.authservicesrc.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static am.armeniabank.authservicesrc.kafka.util.HandlerUtil.processUserEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeleteHandler implements UserEventHandler {

    private final MailService mailService;
    private final UserRepository userRepository;

    @Override
    public boolean isHandle(UserEvent event) {
        return event != null && UserEventType.DELETED.equals(event.getType());
    }

    @Override
    public void handle(UserEvent event) {

        if (event == null) {
            log.warn("Received null UserEvent");
            return;
        }

        if (event.getEmail() == null || event.getEmail().isBlank()) {
            log.warn("UserEvent email is null or blank: {}", event);
            return;
        }

        log.info("User Delete event received for email: {}", event.getEmail());

        processUserEvent(event, userRepository, mailService, log);
    }
}
