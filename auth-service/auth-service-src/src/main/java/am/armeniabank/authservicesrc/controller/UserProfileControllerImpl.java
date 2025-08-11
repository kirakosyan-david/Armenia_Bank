package am.armeniabank.authservicesrc.controller;

import am.armeniabank.authserviceapi.contract.UserProfileController;
import am.armeniabank.authserviceapi.request.UserProfileRequest;
import am.armeniabank.authserviceapi.request.UserProfileUpdateRequest;
import am.armeniabank.authserviceapi.response.UserProfileResponse;
import am.armeniabank.authservicesrc.exception.custom.UserServerError;
import am.armeniabank.authservicesrc.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserProfileControllerImpl implements UserProfileController {

    private final UserProfileService userProfileService;

    @Override
    public ResponseEntity<UserProfileResponse> saveUserProfile(UUID userId, UserProfileRequest requestDto) {

        try {
            UserProfileResponse profile = userProfileService.createProfile(userId, requestDto);
            return ResponseEntity.ok(profile);
        } catch (UserServerError e) {
            log.error("UserServerError during profile update for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error during profile update for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<UserProfileResponse> updateUserProfile(UUID userId, UserProfileUpdateRequest requestDto) {

        try {
            UserProfileResponse profile = userProfileService.updateProfile(userId, requestDto);
            return ResponseEntity.ok(profile);
        } catch (UserServerError e) {
            log.error("UserServerError during profile update for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error during profile update for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<UserProfileResponse> getUserProfile(UUID userId) {

        try {
            UserProfileResponse profileByUserId = userProfileService.getProfileByUserId(userId);
            return ResponseEntity.ok(profileByUserId);
        } catch (UserServerError e) {
            log.error("UserServerError during profile update for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error during profile update for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
