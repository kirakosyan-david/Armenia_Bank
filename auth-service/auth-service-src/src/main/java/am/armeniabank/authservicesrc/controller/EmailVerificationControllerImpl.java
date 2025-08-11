package am.armeniabank.authservicesrc.controller;

import am.armeniabank.authserviceapi.contract.EmailVerificationController;
import am.armeniabank.authservicesrc.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailVerificationControllerImpl implements EmailVerificationController {

    private final MailService mailService;

    @Override
    public ResponseEntity<String> verifyEmail(String email, String token) {
        try {
            mailService.verifyEmail(email, token);
            return ResponseEntity.ok("Email verification successful");
        } catch (IllegalArgumentException | UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed: " + ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> verifyUpdateEmail(String email, String token) {
        try {
            String message = mailService.verifyEmail(email, token);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException | UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed: " + ex.getMessage());
        }
    }
}
