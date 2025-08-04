package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.cilent.AuditClient;
import am.armeniabank.authservice.dto.AuditEventDto;
import am.armeniabank.authservice.dto.LoginRequestDto;
import am.armeniabank.authservice.dto.RefreshTokenRequestDto;
import am.armeniabank.authservice.dto.TokenResponseDto;
import am.armeniabank.authservice.dto.UserEmailSearchResponseDto;
import am.armeniabank.authservice.service.AuthService;
import am.armeniabank.authservice.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

    private final AuditClient auditClient;

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
    public TokenResponseDto login(LoginRequestDto login) {
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
            log.info("Token response JSON: {}", responseBody);

            TokenResponseDto tokenResponse = objectMapper.readValue(responseBody, TokenResponseDto.class);

            UserEmailSearchResponseDto userDto = userService.searchByEmail(login.getEmail());
            userService.updateLastLogin(userDto.getId());

            AuditEventDto auditEvent = new AuditEventDto(
                    "auth-service",
                    "USER_ONELOGIN",
                    "User login with username: " + login.getEmail(),
                    LocalDateTime.now()
            );
            auditClient.sendAuditEvent(auditEvent);

            return tokenResponse;

        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error("Unauthorized: {}", ex.getResponseBodyAsString());
            throw new RuntimeException("Unauthorized: " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            log.error("Login error: {}", ex.getMessage());
            throw new RuntimeException("Login error: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void logout(RefreshTokenRequestDto refreshToken) {
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
            log.error("Logout failed: {}", ex.getResponseBodyAsString());
            throw new RuntimeException("Logout failed: " + ex.getResponseBodyAsString());
        }
    }

    @Override
    public String refreshAccessToken(RefreshTokenRequestDto refreshToken) {

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
            ResponseEntity<TokenResponseDto> response = restTemplate.postForEntity(
                    keycloakBaseUrl + tokenUri,
                    request,
                    TokenResponseDto.class
            );
            return response.getBody().getAccessToken();
        } catch (HttpClientErrorException ex) {
            log.error("Token refresh failed: {}", ex.getResponseBodyAsString());
            throw new RuntimeException("Token refresh failed: " + ex.getResponseBodyAsString());
        }
    }
}
