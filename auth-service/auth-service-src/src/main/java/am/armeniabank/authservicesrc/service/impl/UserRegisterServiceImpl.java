package am.armeniabank.authservicesrc.service.impl;

import am.armeniabank.authserviceapi.emuns.Gender;
import am.armeniabank.authserviceapi.emuns.UserRole;
import am.armeniabank.authserviceapi.request.UserRegistrationRequest;
import am.armeniabank.authserviceapi.response.UserDto;
import am.armeniabank.authservicesrc.entity.User;
import am.armeniabank.authservicesrc.entity.UserProfile;
import am.armeniabank.authservicesrc.exception.custom.EmailAlreadyExistsException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakUserCreationException;
import am.armeniabank.authservicesrc.exception.custom.UserProfileException;
import am.armeniabank.authservicesrc.handler.UserEventHandler;
import am.armeniabank.authservicesrc.integration.AuditServiceClient;
import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
import am.armeniabank.authservicesrc.kafka.model.UserEvent;
import am.armeniabank.authservicesrc.kafka.model.enumeration.UserEventType;
import am.armeniabank.authservicesrc.mapper.UserMapper;
import am.armeniabank.authservicesrc.repository.UserRepository;
import am.armeniabank.authservicesrc.service.KeycloakService;
import am.armeniabank.authservicesrc.service.MailService;
import am.armeniabank.authservicesrc.service.UserRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class UserRegisterServiceImpl implements UserRegisterService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditServiceClient auditClient;
    private final KeycloakService keycloakService;
    private final MailService mailService;
    private final UserEventHandler userEventHandler;

    @Autowired
    public UserRegisterServiceImpl(UserRepository userRepository,
                                   UserMapper userMapper,
                                   PasswordEncoder passwordEncoder,
                                   AuditServiceClient auditClient,
                                   KeycloakService keycloakService,
                                   MailService mailService,
                                   @Qualifier("userCreateHandler") UserEventHandler userEventHandler) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditClient = auditClient;
        this.keycloakService = keycloakService;
        this.mailService = mailService;
        this.userEventHandler = userEventHandler;
    }

    @Override
    public UserDto register(UserRegistrationRequest register) {
        log.info("Registering user: {}", register.getEmail());

        if (userRepository.existsByEmail(register.getEmail())) {
            log.warn("Email {} already exists in database", register.getEmail());
            throw new EmailAlreadyExistsException("Email already exists in database");
        }

        if (keycloakService.emailExistsInKeycloak(register.getEmail())) {
            log.warn("Email {} already exists in Keycloak", register.getEmail());
            throw new EmailAlreadyExistsException("Email already exists in Keycloak");
        }

        UUID keycloakUserId;
        try {
            keycloakUserId = keycloakService.createUserInKeycloak(register, UserRole.USER);
            log.info("User created in Keycloak with ID: {}", keycloakUserId);
        } catch (Exception e) {
            log.error("Failed to create user in Keycloak: {}", e.getMessage(), e);
            throw new KeycloakUserCreationException("Failed to create user in Keycloak", e);
        }

        UserDto savedUserDto;
        try {
            savedUserDto = saveUserInDb(register, keycloakUserId);
        } catch (Exception e) {
            log.error("Failed to save user in DB: {}", e.getMessage(), e);
            throw new UserProfileException("Failed to register user " + register.getEmail(), e);
        }

        return savedUserDto;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto saveUserInDb(UserRegistrationRequest register, UUID keycloakUserId) {
        User user = User.builder()
                .id(keycloakUserId)
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

        User savedUser = userRepository.save(user);
        log.info("User saved in DB with ID: {}", savedUser.getId());

        mailService.sendVerificationEmail(savedUser, register.getPassportNumber());
        log.info("Verification email sent for user: {}", register.getEmail());

        auditEvetConsumer(register);

        userEventProducer(savedUser);

        return userMapper.toDto(savedUser, profile, savedUser.getVerification());
    }

    private void auditEvetConsumer(UserRegistrationRequest register) {
        try {
            AuditEvent auditEvent = new AuditEvent(
                    "auth-service",
                    "USER_REGISTERED",
                    "User Register with username: " + register.getEmail(),
                    LocalDateTime.now()
            );

            auditClient.sendAuditEvent(auditEvent);
            log.info("Audit event sent for email={}", register.getEmail());

        } catch (Exception e) {
            log.error("Failed to send audit event for email={}: {}", register.getEmail(), e.getMessage(), e);
        }

    }

    private void userEventProducer(User userSaved) {
        try {
            UserEvent userEvent = UserEvent.builder()
                    .id(userSaved.getId())
                    .firstName(userSaved.getProfile().getFirstName())
                    .lastName(userSaved.getProfile().getLastName())
                    .patronymic(userSaved.getProfile().getPatronymic())
                    .email(userSaved.getEmail())
                    .emailVerified(userSaved.isEmailVerified())
                    .role(userSaved.getRole().name())
                    .type(UserEventType.CREATED)
                    .createdAt(LocalDateTime.now())
                    .build();
            userEventHandler.handle(userEvent);
            log.info("User event sent for userId={}", userSaved.getId());
        } catch (Exception e) {
            log.error("Failed to send user event for userId={}: {}", userSaved.getId(), e.getMessage(), e);
        }

    }

}
