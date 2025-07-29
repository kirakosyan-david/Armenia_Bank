package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.dto.AuditEventDto;
import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.entity.User;
import am.armeniabank.authservice.entity.UserProfile;
import am.armeniabank.authservice.entity.emuns.Gender;
import am.armeniabank.authservice.entity.emuns.UserRoles;
import am.armeniabank.authservice.mapper.UserMapper;
import am.armeniabank.authservice.repository.UserProfileRepository;
import am.armeniabank.authservice.repository.UserRepository;
import am.armeniabank.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserProfileRepository userProfileRepository;

    private final PasswordEncoder passwordEncoder;

    private final WebClient auditWebClient;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto register(UserRegistrationRequest register) {
        log.info("Attempting to register user using email: {}", register.getEmail());

        if (userRepository.existsByEmail(register.getEmail())) {
            log.warn("Registration failed - email address already in use: {}", register.getEmail());
            throw new RuntimeException("Email already used");
        }
        User user = User.builder()
                .email(register.getEmail())
                .password(passwordEncoder.encode(register.getPassword()))
                .passportNumber(register.getPassportNumber())
                .role(UserRoles.USER)
                .emailVerified(false)
                .build();

        UserProfile userProfile = UserProfile.builder()
                .lastName(register.getLastName())
                .firstName(register.getFirstName())
                .gender(Gender.OTHER)
                .user(user)
                .build();

        user.setProfile(userProfile);
        userRepository.save(user);
        log.info("User successfully registered with ID: {}", user.getId());

        UserDto userDto = userMapper.toDto(user, userProfile);

        sendAuditEvent(userDto);

        return userDto;
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

    private void sendAuditEvent(UserDto userDto) {
        auditWebClient.post()
                .uri("/audit")
                .bodyValue(new AuditEventDto(
                        "auth-service",
                        "USER_REGISTERED",
                        "User registered with email: " + userDto.getEmail(),
                        LocalDateTime.now()
                ))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

}
