package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.entity.emuns.UserRole;
import am.armeniabank.authservice.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

        return response.getBody() != null && !response.getBody().isEmpty();
    }

    @Override
    public void createUserInKeycloak(UserRegistrationRequest register, UserRole role) {
        String accessToken = getAdminAccessToken();

        Map<String, Object> payload = new HashMap<>();
        payload.put("username", "user-" + UUID.randomUUID());
        payload.put("email", register.getEmail());
        payload.put("firstName", register.getFirstName());
        payload.put("lastName", register.getLastName());
        payload.put("enabled", true);
        payload.put("emailVerified", true);
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
            throw new RuntimeException("Failed to retrieve user ID from Keycloak response");
        }

        String userId = location.substring(location.lastIndexOf("/") + 1);
        log.info("Keycloak user created with ID: {}", userId);

        String roleName = (role == UserRole.ADMIN) ? "ROLE_ADMIN" : "ROLE_USER";
        HttpEntity<Void> getRoleRequest = new HttpEntity<>(headers);
        ResponseEntity<Map> roleResponse = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/roles/" + roleName,
                HttpMethod.GET,
                getRoleRequest,
                Map.class
        );

        Map<String, Object> roleMap = roleResponse.getBody();
        if (roleMap == null || roleMap.isEmpty()) {
            throw new RuntimeException("Failed to retrieve role: " + roleName);
        }

        HttpEntity<List<Map<String, Object>>> assignRoleRequest =
                new HttpEntity<>(List.of(roleMap), headers);

        restTemplate.postForEntity(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm",
                assignRoleRequest,
                Void.class
        );
    }

    @Override
    public void updateUserInKeycloak(String currentEmail, UserUpdateRequest request, UserRole role) {
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
            throw new RuntimeException("User not found in Keycloak with email: " + currentEmail);
        }

        Map<String, Object> user = (Map<String, Object>) users.get(0);
        String userId = (String) user.get("id");

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", userId);
        payload.put("email", request.getEmail());
        payload.put("enabled", true);
        payload.put("emailVerified", true);

        HttpEntity<Map<String, Object>> updateRequest = new HttpEntity<>(payload, headers);
        restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId,
                HttpMethod.PUT,
                updateRequest,
                Void.class
        );

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
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

        String roleName = (role == UserRole.ADMIN) ? "ROLE_ADMIN" : "ROLE_USER";
        ResponseEntity<Map> roleResponse = restTemplate.exchange(
                keycloakBaseUrl + "/admin/realms/" + realm + "/roles/" + roleName,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        Map<String, Object> roleMap = roleResponse.getBody();
        if (roleMap == null || roleMap.isEmpty()) {
            throw new RuntimeException("Failed to retrieve role: " + roleName);
        }

        HttpEntity<List<Map<String, Object>>> assignRoleRequest =
                new HttpEntity<>(List.of(roleMap), headers);

        restTemplate.postForEntity(
                keycloakBaseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm",
                assignRoleRequest,
                Void.class
        );

        log.info("Keycloak user updated: {}", userId);
    }


    private String getAdminAccessToken() {
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

        return (String) response.getBody().get("access_token");
    }
}

