package am.armeniabank.authservicesrc.scheduler;

import am.armeniabank.authservicesrc.service.UserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserVerificationScheduler {

    private final UserVerificationService userVerificationService;

    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void checkAndExpireVerifications() {
        userVerificationService.expireAllOutdatedVerifications();
    }
}
