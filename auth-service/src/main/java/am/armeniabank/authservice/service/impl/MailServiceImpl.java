package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.entity.User;
import am.armeniabank.authservice.entity.UserVerification;
import am.armeniabank.authservice.entity.emuns.DocumentType;
import am.armeniabank.authservice.entity.emuns.RejectionReason;
import am.armeniabank.authservice.entity.emuns.VerificationMethod;
import am.armeniabank.authservice.entity.emuns.VerificationStatus;
import am.armeniabank.authservice.entity.emuns.VerifierType;
import am.armeniabank.authservice.repository.UserRepository;
import am.armeniabank.authservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final MailSender mailSender;
    private final UserRepository userRepository;

    @Value("${app.verification-url}")
    private String verificationUrl;

    @Override
    public void sendVerificationEmail(User user, String passportNumber) {
        String token = UUID.randomUUID().toString();

        UserVerification verification = UserVerification.builder()
                .status(VerificationStatus.PENDING)
                .passportNumber(passportNumber)
                .documentType(DocumentType.PASSPORT)
                .rejectionReason(RejectionReason.REJECTED)
                .verificationMethod(VerificationMethod.MAIL)
                .verifiedByType(VerifierType.HUMAN)
                .verificationToken(token)
                .verificationTokenExpiry(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();

        user.setVerification(verification);
        userRepository.save(user);

        String url = verificationUrl + "?email=" + user.getEmail() + "&token=" + token;
        String body = "Please verify your email by clicking the link: " + url;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Email Verification");
        message.setText(body);

        mailSender.send(message);
    }

    @Override
    public void sendVerificationUpdateEmail(User user) {
        String token = UUID.randomUUID().toString();

        UserVerification verification = user.getVerification();
        if (verification == null) {
            verification = UserVerification.builder()
                    .user(user)
                    .build();
        }

        verification.setVerificationToken(token);
        verification.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(30));

        user.setVerification(verification);
        userRepository.save(user);

        String url = verificationUrl + "/update" + "?email=" + user.getEmail() + "&token=" + token;
        String body = "Please verify your email by clicking the link: " + url;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Email Update Verification");
        message.setText(body);

        mailSender.send(message);
    }


    @Override
    public String verifyEmail(String email, String token) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserVerification verification = user.getVerification();

        if (verification == null || !verification.getVerificationToken().equals(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token");
        }

        if (verification.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token has expired");
        }

        user.setEmailVerified(true);
        verification.setStatus(VerificationStatus.VERIFIED);

        userRepository.save(user);

        return "Email verification successful";
    }

}
