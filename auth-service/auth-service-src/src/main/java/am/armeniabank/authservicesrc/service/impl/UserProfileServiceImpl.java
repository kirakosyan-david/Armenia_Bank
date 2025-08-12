package am.armeniabank.authservicesrc.service.impl;

import am.armeniabank.authserviceapi.emuns.Gender;
import am.armeniabank.authserviceapi.request.UserProfileRequest;
import am.armeniabank.authserviceapi.request.UserProfileUpdateRequest;
import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
import am.armeniabank.authserviceapi.response.UserProfileResponse;
import am.armeniabank.authservicesrc.cilent.AuditClient;
import am.armeniabank.authservicesrc.entity.UserProfile;
import am.armeniabank.authservicesrc.mapper.UserProfileMapper;
import am.armeniabank.authservicesrc.repository.UserProfileRepository;
import am.armeniabank.authservicesrc.service.KeycloakService;
import am.armeniabank.authservicesrc.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final KeycloakService keycloakService;
    private final AuditClient auditClient;

    @Override
    @Cacheable(value = "userById", key = "#userId")
    public UserProfileResponse createProfile(UUID userId, UserProfileRequest profile) {
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

        AuditEvent auditEvent = new AuditEvent(
                "auth-service",
                "USER_PROFILE_CREATED",
                "User Profile Create with username: " + userProfile.getUser().getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);

        return userProfileMapper.toUserProfileDto(savedProfile);
    }

    @Override
    @CacheEvict(value = "userById", key = "#userId")
    public UserProfileResponse updateProfile(UUID userId, UserProfileUpdateRequest profile) {
        UserProfile userProfile = findByUserProfileId(userId);

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

        AuditEvent auditEvent = new AuditEvent(
                "auth-service",
                "USER_PROFILE_UPDATED",
                "User Profile Update with username: " + userProfile.getUser().getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);

        return userProfileMapper.toUserProfileDto(savedProfile);
    }


    @Override
    @CacheEvict(value = "userById", key = "#userId")
    public UserProfileResponse getProfileByUserId(UUID userId) {
        UserProfile userProfile = findByUserProfileId(userId);
        return userProfileMapper.toUserProfileDto(userProfile);
    }

    private UserProfile findByUserProfileId(UUID userId) {
        return userProfileRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + userId + " not found"));
    }
}
