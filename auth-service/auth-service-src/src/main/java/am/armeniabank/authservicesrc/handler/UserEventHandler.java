package am.armeniabank.authservicesrc.handler;

import am.armeniabank.authservicesrc.kafka.model.UserEvent;

public interface UserEventHandler {

    boolean isHandle(UserEvent event);

    void handle(UserEvent event);
}
