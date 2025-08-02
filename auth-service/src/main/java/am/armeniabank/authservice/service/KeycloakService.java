package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.entity.emuns.UserRole;

public interface KeycloakService {
    boolean emailExistsInKeycloak(String email);

    void createUserInKeycloak(UserRegistrationRequest register, UserRole role);

    void updateUserInKeycloak(String currentEmail, UserUpdateRequest request, UserRole role);
}
