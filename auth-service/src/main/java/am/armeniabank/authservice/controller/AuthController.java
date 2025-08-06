package am.armeniabank.authservice.controller;

import am.armeniabank.authservice.dto.LoginRequestDto;
import am.armeniabank.authservice.dto.RefreshTokenRequestDto;
import am.armeniabank.authservice.dto.TokenResponseDto;
import am.armeniabank.authservice.dto.UpdateUserDto;
import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserEmailSearchResponseDto;
import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.dto.UserResponseDto;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.exception.custom.SearchEmailException;
import am.armeniabank.authservice.exception.custom.UserLoginException;
import am.armeniabank.authservice.exception.custom.UserServerError;
import am.armeniabank.authservice.exception.custom.WrongUserIdException;
import am.armeniabank.authservice.security.CurrentUser;
import am.armeniabank.authservice.service.AuthService;
import am.armeniabank.authservice.service.UserRegisterService;
import am.armeniabank.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
@Slf4j
public class AuthController {

    private final UserRegisterService userRegisterService;
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegistrationRequest register) {
        try {
            UserDto result = userRegisterService.register(register);
            return ResponseEntity.ok(result);
        } catch (UserServerError e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto login) {
        try {
            TokenResponseDto token = authService.login(login);
            return ResponseEntity.ok(token);
        } catch (UserLoginException e) {
            log.error("Login failed for user {}: {}", login.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<UserEmailSearchResponseDto> searchEmail(@RequestParam String email) {
        try {
            UserEmailSearchResponseDto dto = userService.searchByEmail(email);
            return ResponseEntity.ok(dto);
        } catch (SearchEmailException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserServerError e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable UUID id, @AuthenticationPrincipal CurrentUser currentUser) {
        try {
            UserResponseDto user = userService.findById(id, currentUser);
            return ResponseEntity.ok(user);
        } catch (WrongUserIdException e) {
            log.error("Id failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UpdateUserDto> updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest request) {
        try {
            UpdateUserDto user = userService.updateUser(id, request);
            return ResponseEntity.ok(user);
        } catch (UserServerError e) {
            log.error("Id failed for user update {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (UserServerError e) {
            log.error("Failed to delete user with ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to delete user"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam RefreshTokenRequestDto refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshToken(@Valid @RequestParam RefreshTokenRequestDto refreshToken) {
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

}
