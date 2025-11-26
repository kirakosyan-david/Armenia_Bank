package am.armeniabank.templateservice.service;

import am.armeniabank.templateservice.request.LoginRequest;
import am.armeniabank.templateservice.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${auth-service.url}")
    private String authServiceBaseUrl;

    public TokenResponse loginUser(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                authServiceBaseUrl + "/api/login",
                request,
                TokenResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Login failed");
        }
    }
}
