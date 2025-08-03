package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.cilent.AuditClient;
import am.armeniabank.authservice.dto.AuditEventDto;
import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.entity.User;
import am.armeniabank.authservice.entity.UserProfile;
import am.armeniabank.authservice.entity.emuns.Gender;
import am.armeniabank.authservice.entity.emuns.UserRole;
import am.armeniabank.authservice.mapper.UserMapper;
import am.armeniabank.authservice.repository.UserRepository;
import am.armeniabank.authservice.service.KeycloakService;
import am.armeniabank.authservice.service.MailService;
import am.armeniabank.authservice.service.UserRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegisterServiceImpl implements UserRegisterService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuditClient auditClient;

    private final KeycloakService keycloakService;

    private final MailService mailService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto register(UserRegistrationRequest register) {
        log.info("Registering user: {}", register.getEmail());

        if (userRepository.existsByEmail(register.getEmail())) {
            throw new RuntimeException("Email already exists in DB");
        }

        if (keycloakService.emailExistsInKeycloak(register.getEmail())) {
            throw new RuntimeException("Email already exists in Keycloak");
        }

        User user = User.builder()
                .email(register.getEmail())
                .password(passwordEncoder.encode(register.getPassword()))
                .role(UserRole.USER)
                .emailVerified(false)
                .build();

        UserProfile profile = UserProfile.builder()
                .firstName(register.getFirstName())
                .lastName(register.getLastName())
                .patronymic(register.getPatronymic())
                .gender(Gender.OTHER)
                .user(user)
                .build();

        user.setProfile(profile);

        userRepository.save(user);

        mailService.sendVerificationEmail(user, register.getPassportNumber());

        keycloakService.createUserInKeycloak(register, user.getRole());

        AuditEventDto auditEvent = new AuditEventDto(
                "auth-service",
                "USER_REGISTERED",
                "User Register with username: " + register.getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);

        return userMapper.toDto(user, profile, user.getVerification());
    }

}
