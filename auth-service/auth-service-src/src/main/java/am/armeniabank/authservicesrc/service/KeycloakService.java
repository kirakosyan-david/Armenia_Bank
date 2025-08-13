package am.armeniabank.authservicesrc.service;

import am.armeniabank.authserviceapi.request.UserRegistrationRequest;
import am.armeniabank.authserviceapi.request.UserUpdateRequest;
import am.armeniabank.authserviceapi.emuns.UserRole;

import java.util.UUID;

public interface KeycloakService {

    boolean emailExistsInKeycloak(String email);

    void createUserInKeycloak(UserRegistrationRequest register, UserRole role);

    void updateUserInKeycloak(String currentEmail, UserUpdateRequest request, UserRole role);

    void updateEmailVerified(String email, boolean verified);

    boolean deleteUserFromKeycloak(String email);

    void updateKeycloakUserProfile(UUID userId, String newFirstName, String newLastName);

    String getKeycloakUserIdByEmail(String email);

}
