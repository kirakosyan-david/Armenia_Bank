package am.armeniabank.authservice.service;

import am.armeniabank.authservice.entity.UserProfile;

import java.util.UUID;

public interface UserProfileService {

    UserProfile createProfile(UUID userId, UserProfile profile);

    UserProfile updateProfile(UUID userId, UserProfile profile);

    UserProfile getProfileByUserId(UUID userId);

    void deleteProfile(UUID userId);

}