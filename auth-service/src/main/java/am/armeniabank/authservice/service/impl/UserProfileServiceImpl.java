package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.entity.UserProfile;
import am.armeniabank.authservice.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    @Override
    public UserProfile createProfile(UUID userId, UserProfile profile) {
        return null;
    }

    @Override
    public UserProfile updateProfile(UUID userId, UserProfile profile) {
        return null;
    }

    @Override
    public UserProfile getProfileByUserId(UUID userId) {
        return null;
    }

    @Override
    public void deleteProfile(UUID userId) {

    }
}
