package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.entity.emuns.UserRole;

import java.util.UUID;

public interface KeycloakService {

    boolean emailExistsInKeycloak(String email);

    void createUserInKeycloak(UserRegistrationRequest register, UserRole role);

    void updateUserInKeycloak(String currentEmail, UserUpdateRequest request, UserRole role);

    boolean deleteUserFromKeycloak(String email);

    void updateKeycloakUserProfile(UUID userId, String newFirstName, String newLastName);

    String getKeycloakUserIdByEmail(String email);
}
