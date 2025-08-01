package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.cilent.AuditClient;
import am.armeniabank.authservice.dto.AuditEventDto;
import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.entity.User;
import am.armeniabank.authservice.entity.UserProfile;
import am.armeniabank.authservice.entity.UserVerification;
import am.armeniabank.authservice.entity.emuns.DocumentType;
import am.armeniabank.authservice.entity.emuns.Gender;
import am.armeniabank.authservice.entity.emuns.RejectionReason;
import am.armeniabank.authservice.entity.emuns.UserRole;
import am.armeniabank.authservice.entity.emuns.VerificationMethod;
import am.armeniabank.authservice.entity.emuns.VerificationStatus;
import am.armeniabank.authservice.entity.emuns.VerifierType;
import am.armeniabank.authservice.mapper.UserMapper;
import am.armeniabank.authservice.repository.UserRepository;
import am.armeniabank.authservice.service.UserRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegisterServiceImpl implements UserRegisterService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuditClient auditClient;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public UserDto register(UserRegistrationRequest register) {
        log.info("Registering user: {}", register.getEmail());

        if (userRepository.existsByEmail(register.getEmail())) {
            throw new RuntimeException("Email already exists in DB");
        }

        if (emailExistsInKeycloak(register.getEmail())) {
            throw new RuntimeException("Email already exists in Keycloak");
        }

        User user = User.builder()
                .email(register.getEmail())
                .password(passwordEncoder.encode(register.getPassword()))
                .role(UserRole.USER)
                .emailVerified(true)
                .build();

        UserProfile profile = UserProfile.builder()
                .firstName(register.getFirstName())
                .lastName(register.getLastName())
                .patronymic(register.getPatronymic())
                .gender(Gender.OTHER)
                .user(user)
                .build();

        UserVerification verification = UserVerification.builder()
                .status(VerificationStatus.PENDING)
                .passportNumber(register.getPassportNumber())
                .documentType(DocumentType.PASSPORT)
                .rejectionReason(RejectionReason.REJECTED)
                .verificationMethod(VerificationMethod.MAIL)
                .verifiedByType(VerifierType.HUMAN)
                .user(user)
                .build();

        user.setProfile(profile);
        user.setVerification(verification);
        userRepository.save(user);

        createUserInKeycloak(register, user.getRole());

        AuditEventDto auditEvent = new AuditEventDto(
                "auth-service",
                "USER_REGISTERED",
                "User Register with username: " + register.getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);

        return userMapper.toDto(user, profile, verification);
    }

    private boolean emailExistsInKeycloak(String email) {
        String accessToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users?email=" + email,
                HttpMethod.GET,
                entity,
                List.class
        );

        return response.getBody() != null && !response.getBody().isEmpty();
    }

    private void createUserInKeycloak(UserRegistrationRequest register, UserRole role) {
        String accessToken = getAdminAccessToken();

        // 1. Создание пользователя
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", register.getEmail());
        payload.put("email", register.getEmail());
        payload.put("firstName", register.getFirstName());
        payload.put("lastName", register.getLastName());
        payload.put("enabled", true);
        payload.put("emailVerified", true);
        payload.put("credentials", List.of(Map.of(
                "type", "password",
                "value", register.getPassword(),
                "temporary", false
        )));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users",
                request,
                Void.class
        );

        String location = response.getHeaders().getFirst("Location");
        if (location == null || !location.contains("/")) {
            throw new RuntimeException("Failed to retrieve user ID from Keycloak response");
        }

        String userId = location.substring(location.lastIndexOf("/") + 1);
        log.info("Keycloak user created with ID: {}", userId);

        String roleName = (role == UserRole.ADMIN) ? "ROLE_ADMIN" : "ROLE_USER";
        HttpEntity<Void> getRoleRequest = new HttpEntity<>(headers);
        ResponseEntity<Map> roleResponse = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/roles/" + roleName,
                HttpMethod.GET,
                getRoleRequest,
                Map.class
        );

        Map<String, Object> roleMap = roleResponse.getBody();
        if (roleMap == null || roleMap.isEmpty()) {
            throw new RuntimeException("Failed to retrieve role: " + roleName);
        }

        HttpEntity<List<Map<String, Object>>> assignRoleRequest =
                new HttpEntity<>(List.of(roleMap), headers);

        restTemplate.postForEntity(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm",
                assignRoleRequest,
                Void.class
        );
    }


    private String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", "admin-cli");
        formData.add("username", "admin");
        formData.add("password", "admin"); // ⚠️ Избегай хардкода

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                keycloakBaseUrl + "/realms/master/protocol/openid-connect/token",
                request,
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }
}
