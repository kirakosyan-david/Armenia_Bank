package am.armeniabank.authservicesrc.service;

import am.armeniabank.authserviceapi.response.UserProfileResponse;
import am.armeniabank.authserviceapi.request.UserProfileRequest;
import am.armeniabank.authserviceapi.request.UserProfileUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserProfileService {

    UserProfileResponse createProfile(UUID userId, UserProfileRequest profile);

    UserProfileResponse updateProfile(UUID userId, UserProfileUpdateRequest profile);

    UserProfileResponse getProfileByUserId(UUID userId);

    List<UserProfileResponse> getProfilesPaginated(int page, int size);

}