package am.armeniabank.authservicesrc.service.impl;

import am.armeniabank.authserviceapi.emuns.Gender;
import am.armeniabank.authserviceapi.emuns.UserRole;
import am.armeniabank.authserviceapi.request.UserRegistrationRequest;
import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
import am.armeniabank.authserviceapi.response.UserDto;
import am.armeniabank.authservicesrc.cilent.AuditClient;
import am.armeniabank.authservicesrc.entity.User;
import am.armeniabank.authservicesrc.entity.UserProfile;
import am.armeniabank.authservicesrc.handler.UserEventHandler;
import am.armeniabank.authservicesrc.kafka.enumeration.UserEventType;
import am.armeniabank.authservicesrc.kafka.model.UserEvent;
import am.armeniabank.authservicesrc.mapper.UserMapper;
import am.armeniabank.authservicesrc.repository.UserRepository;
import am.armeniabank.authservicesrc.service.KeycloakService;
import am.armeniabank.authservicesrc.service.MailService;
import am.armeniabank.authservicesrc.service.UserRegisterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserRegisterServiceImpl implements UserRegisterService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuditClient auditClient;

    private final KeycloakService keycloakService;

    private final MailService mailService;

    private final UserEventHandler userEventHandler;

    @Autowired
    public UserRegisterServiceImpl(UserRepository userRepository,
                                   UserMapper userMapper,
                                   PasswordEncoder passwordEncoder,
                                   AuditClient auditClient,
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

        User userSaved = userRepository.save(user);

        mailService.sendVerificationEmail(user, register.getPassportNumber());

        keycloakService.createUserInKeycloak(register, user.getRole());

        auditEvetConsumer(register);

        userEventProducer(userSaved);

        return userMapper.toDto(user, profile, user.getVerification());
    }

    private void auditEvetConsumer(UserRegistrationRequest register) {
        AuditEvent auditEvent = new AuditEvent(
                "auth-service",
                "USER_REGISTERED",
                "User Register with username: " + register.getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);
    }

    private void userEventProducer(User userSaved) {
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
    }

}
