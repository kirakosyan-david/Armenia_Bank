package am.armeniabank.authservice.controller;

import am.armeniabank.authservice.service.MailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verify/email")
@SecurityRequirement(name = "keycloak")
public class EmailVerificationController {

    private final MailService mailService;

    @GetMapping
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String token) {
        try {
            mailService.verifyEmail(email, token);
            return ResponseEntity.ok("Email verification successful");
        } catch (IllegalArgumentException | UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed: " + ex.getMessage());
        }
    }

    @GetMapping("/update")
    public ResponseEntity<String> verifyUpdateEmail(@RequestParam String email, @RequestParam String token) {
        try {
            String message = mailService.verifyEmail(email, token);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException | UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed: " + ex.getMessage());
        }
    }
}
