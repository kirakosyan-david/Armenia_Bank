package am.armeniabank.authservicesrc.service.impl;

import am.armeniabank.authserviceapi.emuns.UserRole;
import am.armeniabank.authserviceapi.request.UserRegistrationRequest;
import am.armeniabank.authserviceapi.request.UserUpdateRequest;
import am.armeniabank.authservicesrc.exception.custom.KeycloakAccessTokenException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakDeleteException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakRoleNotFoundException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakUpdateException;
import am.armeniabank.authservicesrc.exception.custom.KeycloakUserCreationException;
import am.armeniabank.authservicesrc.exception.custom.UserNotFoundException;
import am.armeniabank.authservicesrc.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakServiceImpl implements KeycloakService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Override
    public boolean emailExistsInKeycloak(String email) {
        log.debug("Checking if email {} exists in Keycloak", email);

        String accessToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users?email=" + email,
                HttpMethod.GET,
                entity,
                List.class
        );

        boolean exists = response.getBody() != null && !response.getBody().isEmpty();
        log.info("Email {} exists in Keycloak: {}", email, exists);
        return exists;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createUserInKeycloak(UserRegistrationRequest register, UserRole role) {
        log.info("Creating user {} in Keycloak with role {}", register.getEmail(), role);

        String accessToken = getAdminAccessToken();

        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "user-" + UUID.randomUUID());
        payload.put("email", register.getEmail());
        payload.put("firstName", register.getFirstName());
        payload.put("lastName", register.getLastName());
        payload.put("enabled", true);
        payload.put("emailVerified", register.isEmailVerified());
        payload.put("credentials", List.of(Map.of(
                "type", "password",
                "value", register.getPassword(),
                "temporary", false
        )));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users",
                request,
                Void.class
        );

        String location = response.getHeaders().getFirst("Location");
        if (location == null || !location.contains("/")) {
            log.error("Failed to create user in Keycloak: Location header missing");
            throw new KeycloakUserCreationException("Location header missing after user creation");
        }

        String userId = location.substring(location.lastIndexOf("/") + 1);
        log.info("Keycloak user created with ID: {}", userId);

        assignRoleToUser(role, headers, userId);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateUserInKeycloak(String currentEmail, UserUpdateRequest request, UserRole role) {
        log.info("Updating Keycloak user with email: {}", currentEmail);

        String accessToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> searchRequest = new HttpEntity<>(headers);
        ResponseEntity<List> searchResponse = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users?email=" + currentEmail,
                HttpMethod.GET,
                searchRequest,
                List.class
        );

        List<?> users = searchResponse.getBody();
        if (users == null || users.isEmpty()) {
            log.error("User not found in Keycloak: {}", currentEmail);
            throw new UserNotFoundException("User not found in Keycloak with email: " + currentEmail);
        }

        Map<String, Object> user = (Map<String, Object>) users.get(0);
        String userId = (String) user.get("id");

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", userId);
        payload.put("email", request.getEmail());
        payload.put("enabled", true);
        payload.put("emailVerified", request.isEmailVerified());

        HttpEntity<Map<String, Object>> updateRequest = new HttpEntity<>(payload, headers);
        restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId,
                HttpMethod.PUT,
                updateRequest,
                Void.class
        );

        log.info("Updated Keycloak user {}", userId);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            log.debug("Updating password for Keycloak user {}", userId);
            Map<String, Object> passwordPayload = Map.of(
                    "type", "password",
                    "value", request.getPassword(),
                    "temporary", false
            );

            HttpEntity<Map<String, Object>> passwordRequest = new HttpEntity<>(passwordPayload, headers);
            restTemplate.put(
                    keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password",
                    passwordRequest
            );
        }

        assignRoleToUser(role, headers, userId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateEmailVerified(String email, boolean verified) {
        log.info("Updating emailVerified={} for user {}", verified, email);

        String userId = getUserIdByEmail(email);
        if (userId == null) {
            log.error("User with email {} not found in Keycloak", email);
            throw new UserNotFoundException("User not found in Keycloak: " + email);
        }

        String accessToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("emailVerified", verified);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.put(
                    keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId,
                    request
            );
            log.info("Updated email verification status for {}", email);
        } catch (Exception e) {
            log.error("Failed to update email verification for {}: {}", email, e.getMessage());
            throw new KeycloakUpdateException("Failed to update email verification", e);
        }

        log.info("Email verification status updated in Keycloak for user {}: {}", email, verified);
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public boolean deleteUserFromKeycloak(String email) {
        log.info("Deleting user with email {}", email);

        String userId = getUserIdByEmail(email);
        if (userId == null) {
            log.error("User with email {} not found in Keycloak", email);
            throw new UserNotFoundException("User not found in Keycloak: " + email);
        }
        try {
            String accessToken = getAdminAccessToken();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            restTemplate.exchange(
                    keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );

            log.info("User with ID {} deleted from Keycloak", userId);
            return true;
        } catch (Exception e) {
            log.error("Error deleting user {} from Keycloak: {}", email, e.getMessage());
            throw new KeycloakDeleteException("Failed to delete user from Keycloak: " + email, e);
        }
    }

    public void updateKeycloakUserProfile(UUID keycloakUserId, String newFirstName, String newLastName) {
        String adminToken = getAdminAccessToken();

        String url = keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + keycloakUserId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("firstName", newFirstName);
        body.put("lastName", newLastName);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.put(url, request);
    }


    public String getKeycloakUserIdByEmail(String email) {
        String adminToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users?email=" + email,
                HttpMethod.GET,
                entity,
                (Class<List<Map<String, Object>>>) (Class<?>) List.class
        );

        List<Map<String, Object>> users = response.getBody();
        if (users == null || users.isEmpty()) {
            return null;
        }

        return (String) users.get(0).get("id");
    }

    private void assignRoleToUser(UserRole role, HttpHeaders headers, String userId) {
        String roleName = (role == UserRole.ADMIN) ? "ROLE_ADMIN" : "ROLE_USER";

        log.debug("Assigning role {} to user {}", roleName, userId);

        HttpEntity<Void> getRoleRequest = new HttpEntity<>(headers);

        ResponseEntity<Map> roleResponse = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/roles/" + roleName,
                HttpMethod.GET,
                getRoleRequest,
                Map.class
        );

        Map<String, Object> roleMap = roleResponse.getBody();
        if (roleMap == null || roleMap.isEmpty()) {
            log.error("Role {} not found in Keycloak", roleName);
            throw new KeycloakRoleNotFoundException("Failed to retrieve role: " + roleName);
        }

        HttpEntity<List<Map<String, Object>>> assignRoleRequest =
                new HttpEntity<>(List.of(roleMap), headers);

        restTemplate.postForEntity(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm",
                assignRoleRequest,
                Void.class
        );

        log.info("Assigned role {} to user {}", roleName, userId);
    }

    private String getUserIdByEmail(String email) {
        log.debug("Getting Keycloak userId by email {}", email);

        String accessToken = getAdminAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List> response = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users?email=" + email,
                HttpMethod.GET,
                entity,
                List.class
        );

        List<?> users = response.getBody();
        if (users == null || users.isEmpty()) {
            return null;
        }

        Map<String, Object> user = (Map<String, Object>) users.get(0);
        return (String) user.get("id");
    }

    private String getAdminAccessToken() {
        log.debug("Fetching admin access token from Keycloak");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", "admin-cli");
        formData.add("username", "admin");
        formData.add("password", "admin");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                keycloakBaseUrl + "/realms/master/protocol/openid-connect/token",
                request,
                Map.class
        );

        if (response.getBody() == null || response.getBody().get("access_token") == null) {
            log.error("Failed to fetch admin access token from Keycloak");
            throw new KeycloakAccessTokenException("Unable to retrieve admin access token");
        }

        String token = (String) response.getBody().get("access_token");
        log.debug("Admin access token successfully retrieved");
        return token;
    }
}

