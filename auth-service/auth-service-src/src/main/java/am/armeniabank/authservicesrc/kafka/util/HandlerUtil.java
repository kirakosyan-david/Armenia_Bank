package am.armeniabank.authservicesrc.kafka.util;

import am.armeniabank.authservicesrc.kafka.model.UserEvent;
import am.armeniabank.authservicesrc.repository.UserRepository;
import am.armeniabank.authservicesrc.service.MailService;
import org.slf4j.Logger;

public class HandlerUtil {

    private HandlerUtil() {
    }

    public static void processUserEvent(UserEvent event, UserRepository userRepository, MailService mailService, Logger log) {
        userRepository.findByEmail(event.getEmail()).ifPresentOrElse(user -> {

            String passportNumber = user.getVerification() != null ? user.getVerification().getPassportNumber() : null;
            mailService.sendVerificationEmail(user, passportNumber);

            log.info("Verification email sent to {}", event.getEmail());

        }, () -> {
            log.warn("User with email {} not found in database", event.getEmail());
        });
    }
}
