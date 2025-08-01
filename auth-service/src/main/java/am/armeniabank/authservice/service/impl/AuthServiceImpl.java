package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.cilent.AuditClient;
import am.armeniabank.authservice.dto.AuditEventDto;
import am.armeniabank.authservice.dto.LoginRequestDto;
import am.armeniabank.authservice.dto.TokenResponseDto;
import am.armeniabank.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;


    @Override
    public TokenResponseDto login(LoginRequestDto login) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("grant_type", "password");
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("username", login.getEmail());
        params.put("password", login.getPassword());

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

            AuditEventDto auditEvent = new AuditEventDto(
                    "auth-service",
                    "USER_ONELOGIN",
                    "User login with username: " + login.getEmail(),
                    LocalDateTime.now()
            );
            auditClient.sendAuditEvent(auditEvent);

            return response.getBody();

        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error("Unauthorized: {}", ex.getResponseBodyAsString());
            throw new RuntimeException("Unauthorized: " + ex.getResponseBodyAsString());
        }
    }

    @Override
    public void logout(String token) {

    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        return "";
    }
}
