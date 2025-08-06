package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.UserProfileDto;
import am.armeniabank.authservice.dto.UserProfileRequestDto;
import am.armeniabank.authservice.dto.UserProfileUpdateRequestDto;
import am.armeniabank.authservice.entity.UserProfile;

import java.util.UUID;

public interface UserProfileService {

    UserProfileDto createProfile(UUID userId, UserProfileRequestDto profile);

    UserProfileDto updateProfile(UUID userId, UserProfileUpdateRequestDto profile);

    UserProfileDto getProfileByUserId(UUID userId);

}