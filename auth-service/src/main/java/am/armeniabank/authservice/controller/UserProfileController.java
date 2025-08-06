package am.armeniabank.authservice.controller;

import am.armeniabank.authservice.dto.UserProfileDto;
import am.armeniabank.authservice.dto.UserProfileRequestDto;
import am.armeniabank.authservice.dto.UserProfileUpdateRequestDto;
import am.armeniabank.authservice.exception.custom.UserServerError;
import am.armeniabank.authservice.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/profile")
@Validated
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDto> saveUserProfile(@PathVariable("userId") UUID userId,
                                                          @Valid @RequestBody UserProfileRequestDto requestDto) {

        try {
            UserProfileDto profile = userProfileService.createProfile(userId, requestDto);
            return ResponseEntity.ok(profile);
        } catch (UserServerError e) {
            log.error("UserServerError during profile update for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error during profile update for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserProfileDto> updateUserProfile(@PathVariable("userId") UUID userId,
                                                            @Valid @RequestBody UserProfileUpdateRequestDto requestDto) {

        try {
            UserProfileDto profile = userProfileService.updateProfile(userId, requestDto);
            return ResponseEntity.ok(profile);
        } catch (UserServerError e) {
            log.error("UserServerError during profile update for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error during profile update for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable("userId") UUID userId) {

        try {
            UserProfileDto profileByUserId = userProfileService.getProfileByUserId(userId);
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
