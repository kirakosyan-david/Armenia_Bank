package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.cilent.AuditClient;
import am.armeniabank.authservice.dto.AuditEventDto;
import am.armeniabank.authservice.dto.LoginRequestDto;
import am.armeniabank.authservice.dto.TokenResponseDto;
import am.armeniabank.authservice.repository.UserRepository;
import am.armeniabank.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final AuditClient auditClient;

    private final WebClient webClient;

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;


    @Override
    public Mono<TokenResponseDto> login(LoginRequestDto login) {
        Mono<TokenResponseDto> dtoMono = webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("username", login.getEmail())
                        .with("password", login.getPassword()))
                .retrieve()
                .onStatus(httpStatus -> httpStatus.value() == 401, response -> response.bodyToMono(String.class)
                        .doOnNext(error -> log.error("Keycloak 401 error: {}", error))
                        .flatMap(error -> Mono.error(new RuntimeException("Unauthorized: " + error))))
                .bodyToMono(TokenResponseDto.class);

        AuditEventDto auditEvent = new AuditEventDto(
                "auth-service",
                "USER_REGISTERED",
                "User login with username: " + login.getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent).subscribe();

        return dtoMono;
    }

    @Override
    public void logout(String token) {

    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        return "";
    }
}
