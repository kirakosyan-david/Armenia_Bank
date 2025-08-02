package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.UpdateUserDto;
import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserEmailSearchResponseDto;
import am.armeniabank.authservice.dto.UserResponseDto;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.security.CurrentUser;

import java.util.UUID;

public interface UserService {

    UserEmailSearchResponseDto searchByEmail(String email);

    UserResponseDto findById(UUID id, CurrentUser currentUser);

    UpdateUserDto update(UUID id, UserUpdateRequest request);

    boolean existsByEmail(String email);

    void enableUser(UUID userId);

    void lockUser(UUID userId, String reason);

    void resetPassword(UUID userId, String newPassword);

    void updateLastLogin(UUID userId);

    void deleteUser(UUID userId);

}