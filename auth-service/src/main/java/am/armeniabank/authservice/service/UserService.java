package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.UpdateUserDto;
import am.armeniabank.authservice.dto.UserEmailSearchResponseDto;
import am.armeniabank.authservice.dto.UserResponseDto;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.security.CurrentUser;

import java.util.UUID;

public interface UserService {

    UserEmailSearchResponseDto searchByEmail(String email);

    UserResponseDto findById(UUID id, CurrentUser currentUser);

    UpdateUserDto updateUser(UUID id, UserUpdateRequest request);

    void updateLastLogin(UUID userId);

    void deleteUser(UUID userId);

}