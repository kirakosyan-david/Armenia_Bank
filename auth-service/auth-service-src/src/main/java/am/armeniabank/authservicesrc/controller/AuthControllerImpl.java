package am.armeniabank.authservicesrc.controller;

import am.armeniabank.authserviceapi.contract.AuthController;
import am.armeniabank.authserviceapi.request.LoginRequest;
import am.armeniabank.authserviceapi.request.RefreshTokenRequest;
import am.armeniabank.authserviceapi.request.UserRegistrationRequest;
import am.armeniabank.authserviceapi.response.TokenResponse;
import am.armeniabank.authserviceapi.response.UserDto;
import am.armeniabank.authservicesrc.exception.custom.UserLoginException;
import am.armeniabank.authservicesrc.exception.custom.UserServerError;
import am.armeniabank.authservicesrc.service.AuthService;
import am.armeniabank.authservicesrc.service.UserRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthControllerImpl implements AuthController {

    private final UserRegisterService userRegisterService;
    private final AuthService authService;


    @Override
    public ResponseEntity<UserDto> register(UserRegistrationRequest register) {
        try {
            UserDto result = userRegisterService.register(register);
            return ResponseEntity.ok(result);
        } catch (UserServerError e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<TokenResponse> login(LoginRequest login) {
        try {
            TokenResponse token = authService.login(login);
            return ResponseEntity.ok(token);
        } catch (UserLoginException e) {
            log.error("Login failed for user {}: {}", login.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<Void> logout(RefreshTokenRequest refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> refreshToken(RefreshTokenRequest refreshToken) {
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

}
