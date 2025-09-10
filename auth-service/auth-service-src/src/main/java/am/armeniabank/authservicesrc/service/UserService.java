package am.armeniabank.authservicesrc.service;

import am.armeniabank.authserviceapi.request.UserUpdateRequest;
import am.armeniabank.authserviceapi.response.UpdateUserResponse;
import am.armeniabank.authserviceapi.response.UserEmailSearchResponse;
import am.armeniabank.authserviceapi.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserEmailSearchResponse searchByEmail(String email);

    UserResponse findById(UUID id);

    UpdateUserResponse updateUser(UUID id, UserUpdateRequest request);

    void updateLastLogin(UUID userId);

    void deleteUser(UUID userId);

    List<UserResponse> getUsersPaginated(int page, int size);

}