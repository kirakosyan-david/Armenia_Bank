package am.armeniabank.authservicesrc.service;

import am.armeniabank.authservicesrc.entity.User;

public interface MailService {

    void sendVerificationEmail(User user, String passportNumber);

    void sendVerificationUpdateEmail(User user);

    String verifyEmail(String email, String token);

}
