package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserUpdateRequest;

import java.util.UUID;

public interface UserService {

    UserDto findByEmail(String email);

    UserDto findById(UUID id);

    UserDto update(UUID id, UserUpdateRequest request);

    boolean existsByEmail(String email);

    void enableUser(UUID userId);

    void lockUser(UUID userId, String reason);

    void resetPassword(UUID userId, String newPassword);

    void updateLastLogin(UUID userId);

    void deleteUser(UUID userId);

}