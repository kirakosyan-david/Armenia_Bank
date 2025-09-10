package am.armeniabank.authservicesrc.service.impl;

import am.armeniabank.authserviceapi.request.LoginRequest;
import am.armeniabank.authserviceapi.request.RefreshTokenRequest;
import am.armeniabank.authserviceapi.response.TokenResponse;
import am.armeniabank.authserviceapi.response.UserEmailSearchResponse;
import am.armeniabank.authservicesrc.exception.custom.LoginFailedException;
import am.armeniabank.authservicesrc.exception.custom.LogoutFailedException;
import am.armeniabank.authservicesrc.exception.custom.TokenRefreshException;
import am.armeniabank.authservicesrc.integration.AuditServiceClient;
import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
import am.armeniabank.authservicesrc.service.AuthService;
import am.armeniabank.authservicesrc.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuditServiceClient auditClient;

    private final RestTemplate restTemplate;

    private final UserService userService;

    private final ObjectMapper objectMapper;

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;


    @Override
    @Transactional
    public TokenResponse login(LoginRequest login) {
        log.info("Attempting login for user with email={}", login.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("username", login.getEmail());
        params.add("password", login.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    keycloakBaseUrl + tokenUri,
                    request,
                    String.class
            );

            String responseBody = response.getBody();
            log.debug("Keycloak token response raw JSON: {}", responseBody);

            TokenResponse tokenResponse = objectMapper.readValue(responseBody, TokenResponse.class);

            UserEmailSearchResponse userDto = userService.searchByEmail(login.getEmail());
            userService.updateLastLogin(userDto.getId());

            AuditEvent auditEvent = new AuditEvent(
                    "auth-service",
                    "USER_ONELOGIN",
                    "User login with username: " + login.getEmail(),
                    LocalDateTime.now()
            );
            auditClient.sendAuditEvent(auditEvent);

            log.info("User successfully logged in with email={}", login.getEmail());
            return tokenResponse;

        } catch (HttpClientErrorException.Unauthorized ex) {
            log.warn("Unauthorized login attempt for email={}. Error: {}", login.getEmail(), ex.getResponseBodyAsString());
            throw new LoginFailedException("Invalid username or password", ex);
        } catch (Exception ex) {
            log.error("Login error for email={}: {}", login.getEmail(), ex.getMessage(), ex);
            throw new LoginFailedException("Login failed due to unexpected error", ex);
        }
    }

    @Override
    public void logout(RefreshTokenRequest refreshToken) {
        log.info("Attempting logout with refreshToken={}", refreshToken.getRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("refresh_token", refreshToken.getRefreshToken());

        StringBuilder body = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (body.length() > 0) body.append("&");
            body.append(entry.getKey()).append("=").append(entry.getValue());
        }

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

        try {
            restTemplate.postForEntity(
                    keycloakBaseUrl + "/realms/ArmeniaBank/protocol/openid-connect/logout",
                    request,
                    String.class
            );
            log.info("User successfully logged out");
        } catch (HttpClientErrorException ex) {
            log.error("Logout failed for refreshToken={}", refreshToken.getRefreshToken(), ex);
            throw new LogoutFailedException("Logout failed: " + ex.getResponseBodyAsString(), ex);
        }
    }

    @Override
    public String refreshAccessToken(RefreshTokenRequest refreshToken) {
        log.info("Attempting to refresh access token with refreshToken={}", refreshToken.getRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("refresh_token", refreshToken.getRefreshToken());

        StringBuilder body = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (body.length() > 0) body.append("&");
            body.append(entry.getKey()).append("=").append(entry.getValue());
        }

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

        try {
            ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                    keycloakBaseUrl + tokenUri,
                    request,
                    TokenResponse.class
            );
            log.info("Access token refreshed successfully");
            return response.getBody().getAccessToken();
        } catch (HttpClientErrorException ex) {
            log.error("Token refresh failed for refreshToken={}", refreshToken.getRefreshToken(), ex);
            throw new TokenRefreshException("Could not refresh token", ex);
        }
    }
}
