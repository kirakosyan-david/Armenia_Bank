package am.armeniabank.authservice.service;

import am.armeniabank.authservice.entity.User;

public interface MailService {

    void sendVerificationEmail(User user, String passportNumber);

    void sendVerificationUpdateEmail(User user);

    String verifyEmail(String email, String token);

}
