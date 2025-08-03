package am.armeniabank.authservice.service;

import am.armeniabank.authservice.entity.User;

public interface MailService {

    void sendMail(String to, String subject, String text);

    void sendVerificationEmail(User user, String passportNumber);

    void sendVerificationUpdateEmail(User user);

    String verifyEmail(String email, String token);

    void sendFromMail(String fromEmail, String subject, String name, String text);
}
