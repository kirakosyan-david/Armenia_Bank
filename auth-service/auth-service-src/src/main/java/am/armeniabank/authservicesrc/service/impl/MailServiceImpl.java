package am.armeniabank.authservicesrc.service.impl;

import am.armeniabank.authserviceapi.emuns.DocumentType;
import am.armeniabank.authserviceapi.emuns.RejectionReason;
import am.armeniabank.authserviceapi.emuns.VerificationMethod;
import am.armeniabank.authserviceapi.emuns.VerificationStatus;
import am.armeniabank.authserviceapi.emuns.VerifierType;
import am.armeniabank.authservicesrc.entity.User;
import am.armeniabank.authservicesrc.entity.UserVerification;
import am.armeniabank.authservicesrc.repository.UserRepository;
import am.armeniabank.authservicesrc.service.KeycloakService;
import am.armeniabank.authservicesrc.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final MailSender mailSender;
    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    @Value("${app.verification-url}")
    private String verificationUrl;

    @Override
    public void sendVerificationEmail(User user, String passportNumber) {
        String token = UUID.randomUUID().toString();

        UserVerification verification = user.getVerification();
        if (verification == null) {
            verification = UserVerification.builder()
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
        } else {
            verification.setStatus(VerificationStatus.PENDING);
            verification.setPassportNumber(passportNumber);
            verification.setDocumentType(DocumentType.PASSPORT);
            verification.setRejectionReason(RejectionReason.REJECTED);
            verification.setVerificationMethod(VerificationMethod.MAIL);
            verification.setVerifiedByType(VerifierType.HUMAN);
            verification.setVerificationToken(token);
            verification.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(30));
        }

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

        if (user.isEmailVerified()) {
            return "Email is already verified";
        }

        user.setEmailVerified(true);
        verification.setStatus(VerificationStatus.VERIFIED);
        verification.setVerifiedAt(LocalDateTime.now());
        userRepository.save(user);

        try {
            keycloakService.updateEmailVerified(user.getEmail(), true);
        } catch (Exception e) {
            log.warn("Failed to update Keycloak emailVerified for {}: {}", user.getEmail(), e.getMessage());
        }

        return "Email verification successful";
    }

}
