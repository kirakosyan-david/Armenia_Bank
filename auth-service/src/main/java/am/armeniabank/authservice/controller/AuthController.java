package am.armeniabank.authservice.controller;

import am.armeniabank.authservice.dto.LoginRequestDto;
import am.armeniabank.authservice.dto.TokenResponseDto;
import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.service.AuthService;
import am.armeniabank.authservice.service.UserRegisterService;
import am.armeniabank.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
@Slf4j
public class AuthController {

    private final UserRegisterService userRegisterService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegistrationRequest register) {
        try {
            UserDto result = userRegisterService.register(register);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto login) {
        try {
            TokenResponseDto token = authService.login(login);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", login.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
