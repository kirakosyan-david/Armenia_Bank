package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.cilent.AuditClient;
import am.armeniabank.authservice.dto.AuditEventDto;
import am.armeniabank.authservice.dto.UpdateUserDto;
import am.armeniabank.authservice.dto.UserEmailSearchResponseDto;
import am.armeniabank.authservice.dto.UserResponseDto;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.entity.User;
import am.armeniabank.authservice.entity.UserProfile;
import am.armeniabank.authservice.entity.UserVerification;
import am.armeniabank.authservice.entity.emuns.UserRole;
import am.armeniabank.authservice.mapper.UserMapper;
import am.armeniabank.authservice.repository.UserRepository;
import am.armeniabank.authservice.security.CurrentUser;
import am.armeniabank.authservice.service.KeycloakService;
import am.armeniabank.authservice.service.MailService;
import am.armeniabank.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;
    private final AuditClient auditClient;
    private final MailService mailService;

    @Override
    public UserEmailSearchResponseDto searchByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User with email " + email + " not found")
        );
        UserProfile profile = user.getProfile();
        UserVerification verification = user.getVerification();
        return userMapper.toSearchDto(user, profile, verification);
    }

    @Override
    public UserResponseDto findById(UUID id, CurrentUser currentUser) {
        User user = findUserById(id);

        UserProfile profile = user.getProfile();
        UserVerification verification = user.getVerification();

        if (currentUser.getUser().getId().equals(id)) {
            return userMapper.toUserByIdDto(user, profile, verification);
        }
        throw new AccessDeniedException("You are not allowed to view this user’s data");
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UpdateUserDto updateUser(UUID id, UserUpdateRequest request) {

        User userById = findUserById(id);

        String oldEmail = userById.getEmail();

        boolean isEmail = keycloakService.emailExistsInKeycloak(oldEmail);
        if (isEmail) {
            log.error("User with email {} not found in Keycloak", oldEmail);
        }

        userById.setEmail(request.getEmail());
        userById.setPassword(passwordEncoder.encode(request.getPassword()));
        userById.setRole(UserRole.valueOf(request.getRole().name().toUpperCase(Locale.ROOT)));
        userById.setEmailVerified(false);
        userById.setUpdatedAt(LocalDateTime.now());

        User user = userRepository.save(userById);

        mailService.sendVerificationUpdateEmail(user);

        keycloakService.updateUserInKeycloak(oldEmail, request, user.getRole());

        AuditEventDto auditEvent = new AuditEventDto(
                "auth-service",
                "USER_UPDATED",
                "User Update with username: " + request.getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);

        return userMapper.toUserUpdateDto(user, user.getVerification());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateLastLogin(UUID userId) {
        User user = findUserById(userId);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUser(UUID userId) {
        User user = findUserById(userId);

        boolean keycloakDeleted = keycloakService.deleteUserFromKeycloak(user.getEmail());
        if (!keycloakDeleted) {
            log.warn("User not removed from Keycloak: {}", user.getEmail());
        }
        userRepository.deleteById(user.getId());
        log.info("The user with email address {} has been removed from the database.", user.getEmail());
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + id + " not found"));
    }

}
