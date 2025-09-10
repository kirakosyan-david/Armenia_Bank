package am.armeniabank.authservicesrc.service.impl;

import am.armeniabank.authserviceapi.emuns.Gender;
import am.armeniabank.authserviceapi.request.UserProfileRequest;
import am.armeniabank.authserviceapi.request.UserProfileUpdateRequest;
import am.armeniabank.authservicesrc.exception.custom.UserProfileException;
import am.armeniabank.authservicesrc.exception.custom.UserProfileNotFoundException;
import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
import am.armeniabank.authserviceapi.response.UserProfileResponse;
import am.armeniabank.authservicesrc.integration.AuditServiceClient;
import am.armeniabank.authservicesrc.entity.UserProfile;
import am.armeniabank.authservicesrc.mapper.UserProfileMapper;
import am.armeniabank.authservicesrc.repository.UserProfileRepository;
import am.armeniabank.authservicesrc.service.KeycloakService;
import am.armeniabank.authservicesrc.service.UserProfileService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final KeycloakService keycloakService;
    private final AuditServiceClient auditClient;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Cacheable(value = "userById", key = "#userId")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserProfileResponse createProfile(UUID userId, UserProfileRequest profile) {
        log.info("Starting createProfile for userId={}", userId);

        try {
            UserProfile userProfile = findByUserProfileId(userId);

            userProfile.setBirthDate(profile.getBirthDate());
            userProfile.setGender(Gender.valueOf(profile.getGender().name().toUpperCase(Locale.ROOT)));
            userProfile.setPhoneNumber(profile.getPhoneNumber());
            userProfile.setAddress(profile.getAddress());
            userProfile.setNationality(profile.getNationality());
            userProfile.setCitizenship(profile.getCitizenship());
            userProfile.setTimezone(profile.getTimezone());
            userProfile.setPreferredLanguage(profile.getPreferredLanguage());

            UserProfile savedProfile = userProfileRepository.save(userProfile);

            sendAuditEvent(savedProfile, "USER_PROFILE_CREATED");

            log.info("Completed createProfile for userId={}", userId);
            return userProfileMapper.toUserProfileDto(savedProfile);

        } catch (Exception e) {
            log.error("Error creating profile for userId={}: {}", userId, e.getMessage(), e);
            throw new UserProfileException("Failed to create profile for userId=" + userId, e);
        }

    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "userById", key = "#userId")
    public UserProfileResponse updateProfile(UUID userId, UserProfileUpdateRequest profile) {
        log.info("Starting updateProfile for userId={}", userId);
        try {
            UserProfile userProfile = entityManager.find(UserProfile.class, userId, LockModeType.PESSIMISTIC_WRITE);
            if (userProfile == null) {
                throw new UserProfileNotFoundException("UserProfile not found for userId=" + userId);
            }

            userProfile.setFirstName(profile.getFirstName());
            userProfile.setLastName(profile.getLastName());

            userProfile.setPatronymic(profile.getPatronymic());
            userProfile.setBirthDate(profile.getBirthDate());
            userProfile.setGender(Gender.valueOf(profile.getGender().name().toUpperCase(Locale.ROOT)));
            userProfile.setPhoneNumber(profile.getPhoneNumber());
            userProfile.setAddress(profile.getAddress());
            userProfile.setNationality(profile.getNationality());
            userProfile.setCitizenship(profile.getCitizenship());
            userProfile.setTimezone(profile.getTimezone());
            userProfile.setPreferredLanguage(profile.getPreferredLanguage());

            UserProfile savedProfile = userProfileRepository.save(userProfile);

            try {
                String email = userProfile.getUser().getEmail();
                String keycloakUserIdStr = keycloakService.getKeycloakUserIdByEmail(email);

                if (keycloakUserIdStr != null) {
                    UUID keycloakUserId = UUID.fromString(keycloakUserIdStr);
                    keycloakService.updateKeycloakUserProfile(keycloakUserId, profile.getFirstName(), profile.getLastName());
                } else {
                    log.warn("Keycloak user ID not found for email {}", email);
                }
            } catch (Exception e) {
                log.error("Failed to update Keycloak user profile for userId {}", userId, e);
            }

            sendAuditEvent(savedProfile, "USER_PROFILE_UPDATED");

            log.info("Completed updateProfile for userId={}", userId);
            return userProfileMapper.toUserProfileDto(savedProfile);

        } catch (Exception e) {
            log.error("Error updating profile for userId={}: {}", userId, e.getMessage(), e);
            throw new UserProfileException("Failed to update profile for userId=" + userId, e);
        }

    }


    @Override
    @Transactional(readOnly = true)
    @CacheEvict(value = "userById", key = "#userId")
    public UserProfileResponse getProfileByUserId(UUID userId) {
        log.info("Fetching profile for userId={}", userId);
        try {
            UserProfile userProfile = findByUserProfileId(userId);
            return userProfileMapper.toUserProfileDto(userProfile);
        } catch (Exception e) {
            log.error("Error fetching profile for userId={}: {}", userId, e.getMessage(), e);
            throw new UserProfileException("Failed to fetch profile for userId=" + userId, e);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getProfilesPaginated(int page, int size) {
        log.info("Fetching paginated user profiles page={} size={}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
            Page<UserProfile> profilesPage = userProfileRepository.findAll(pageable);
            return profilesPage.stream()
                    .map(userProfileMapper::toUserProfileDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching paginated profiles: {}", e.getMessage(), e);
            throw new UserProfileException("Failed to fetch paginated profiles", e);
        }
    }

    private UserProfile findByUserProfileId(UUID userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> {
            log.warn("UserProfile not found for userId={}", userId);
            return new UsernameNotFoundException("UserProfile with ID " + userId + " not found");
        });
    }

    private void sendAuditEvent(UserProfile profile, String eventType) {
        try {
            AuditEvent auditEvent = new AuditEvent(
                    "auth-service",
                    eventType,
                    "User Profile " + eventType.replace("USER_PROFILE_", "") +
                            " with username: " + profile.getUser().getEmail(),
                    LocalDateTime.now()
            );
            auditClient.sendAuditEvent(auditEvent);
        } catch (Exception e) {
            log.error("Failed to send audit event for userId={}: {}", profile.getUserId(), e.getMessage(), e);
        }
    }
}
