package am.armeniabank.authservicesrc.service.impl;

import am.armeniabank.authserviceapi.emuns.UserRole;
import am.armeniabank.authserviceapi.request.UserUpdateRequest;
import am.armeniabank.authserviceapi.response.AuditEventResponse;
import am.armeniabank.authserviceapi.response.UpdateUserResponse;
import am.armeniabank.authserviceapi.response.UserEmailSearchResponse;
import am.armeniabank.authserviceapi.response.UserResponse;
import am.armeniabank.authservicesrc.cilent.AuditClient;
import am.armeniabank.authservicesrc.entity.User;
import am.armeniabank.authservicesrc.entity.UserProfile;
import am.armeniabank.authservicesrc.entity.UserVerification;
import am.armeniabank.authservicesrc.mapper.UserMapper;
import am.armeniabank.authservicesrc.repository.UserRepository;
import am.armeniabank.authservicesrc.service.KeycloakService;
import am.armeniabank.authservicesrc.service.MailService;
import am.armeniabank.authservicesrc.service.UserService;
import am.armeniabank.authservicesrc.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "userByEmail", key = "#email")
    public UserEmailSearchResponse searchByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User with email " + email + " not found")
        );
        UserProfile profile = user.getProfile();
        UserVerification verification = user.getVerification();
        return userMapper.toSearchDto(user, profile, verification);
    }

    @Override
    @Cacheable(value = "userById", key = "#id")
    public UserResponse findById(UUID id) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User user = findUserById(id);

        UserProfile profile = user.getProfile();
        UserVerification verification = user.getVerification();

        if (currentUserId.equals(id)) {
            return userMapper.toUserByIdDto(user, profile, verification);
        }
        throw new AccessDeniedException("You are not allowed to view this user’s data");
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Caching(evict = {
            @CacheEvict(value = "userByEmail", key = "#request.email"),
            @CacheEvict(value = "userById", key = "#id")
    })
    public UpdateUserResponse updateUser(UUID id, UserUpdateRequest request) {

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

        AuditEventResponse auditEvent = new AuditEventResponse(
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

        evictCacheManual(user.getEmail(), user.getId());
    }

    protected void evictCacheManual(String email, UUID id) {
        if (email != null) {
            var cache = cacheManager.getCache("userByEmail");
            if (cache != null) {
                cache.evict(email);
                log.info("Evicted userByEmail cache for {}", email);
            }
        }
        if (id != null) {
            var cache = cacheManager.getCache("userById");
            if (cache != null) {
                cache.evict(id);
                log.info("Evicted userById cache for {}", id);
            }
        }
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + id + " not found"));
    }

}
