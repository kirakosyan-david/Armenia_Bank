package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.cilent.AuditClient;
import am.armeniabank.authservice.dto.AuditEventDto;
import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.entity.User;
import am.armeniabank.authservice.entity.UserProfile;
import am.armeniabank.authservice.entity.emuns.Gender;
import am.armeniabank.authservice.entity.emuns.UserRole;
import am.armeniabank.authservice.mapper.UserMapper;
import am.armeniabank.authservice.repository.UserProfileRepository;
import am.armeniabank.authservice.repository.UserRepository;
import am.armeniabank.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserProfileRepository userProfileRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuditClient auditClient;

    private final WebClient webClient;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;




    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Mono<UserDto> register(UserRegistrationRequest register) {
        log.info("Attempting to register user using email: {}", register.getEmail());

        return Mono.just(register)
                .filterWhen(reg -> Mono.just(!userRepository.existsByEmail(reg.getEmail()))
                        .switchIfEmpty(Mono.error(new RuntimeException("Email already used"))))
                .doOnNext(reg -> log.warn("Registration failed - email address already in use: {}", reg.getEmail()))
                .switchIfEmpty(Mono.error(new RuntimeException("Email already used")))
                .map(reg -> {
                    User user = User.builder()
                            .email(reg.getEmail())
                            .password(passwordEncoder.encode(reg.getPassword()))
                            .passportNumber(reg.getPassportNumber())
                            .role(UserRole.USER)
                            .emailVerified(true)
                            .build();

                    UserProfile userProfile = UserProfile.builder()
                            .lastName(reg.getLastName())
                            .firstName(reg.getFirstName())
                            .gender(Gender.OTHER)
                            .user(user)
                            .build();

                    user.setProfile(userProfile);
                    return user;
                })
                .flatMap(user -> Mono.just(userRepository.save(user))
                        .doOnSuccess(savedUser -> log.info("User successfully registered with ID: {}", savedUser.getId())))
                .flatMap(user -> syncUserWithKeycloak(user.getEmail(), register.getPassword(), register.getFirstName(), register.getLastName())
                        .thenReturn(user))
                .map(user -> {
                    UserDto userDto = userMapper.toDto(user, user.getProfile());
                    AuditEventDto auditEvent = new AuditEventDto(
                            "auth-service",
                            "USER_REGISTERED",
                            "User registered with email: " + userDto.getEmail(),
                            LocalDateTime.now()
                    );
                    auditClient.sendAuditEvent(auditEvent).subscribe();
                    return userDto;
                });
    }

    private Mono<Void> syncUserWithKeycloak(String username, String password, String firstName, String lastName) {
        return getServiceToken()
                .flatMap(serviceToken -> {
                    Map<String, Object> user = new HashMap<>();
                    user.put("username", username);
                    user.put("email", username);
                    user.put("enabled", true);
                    user.put("firstName", firstName);
                    user.put("lastName", lastName);
                    user.put("emailVerified", true);
                    user.put("credentials", Collections.singletonList(Map.of(
                            "type", "password",
                            "value", password,
                            "temporary", false
                    )));

                    return webClient.post()
                            .uri("/admin/realms/{realm}/users", realm)
                            .header("Authorization", "Bearer " + serviceToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(user)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .doOnSuccess(response -> log.info("User {} synced with Keycloak", username))
                            .doOnError(error -> log.error("Failed to sync user {} with Keycloak: {}", username, error.getMessage()));
                });
    }

    private Mono<String> getServiceToken() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        return webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .cache(); // Кэширование токена для повторного использования
    }

    @Override
    public UserDto findByEmail(String email) {
        return null;
    }

    @Override
    public UserDto findById(UUID id) {
        return null;
    }

    @Override
    public UserDto update(UUID id, UserUpdateRequest request) {
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public void enableUser(UUID userId) {

    }

    @Override
    public void lockUser(UUID userId, String reason) {

    }

    @Override
    public void resetPassword(UUID userId, String newPassword) {

    }

    @Override
    public void updateLastLogin(UUID userId) {

    }

    @Override
    public void deleteUser(UUID userId) {

    }

}
