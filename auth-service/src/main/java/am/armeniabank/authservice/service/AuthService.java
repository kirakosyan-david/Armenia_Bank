package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.LoginRequestDto;
import am.armeniabank.authservice.dto.TokenResponseDto;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<TokenResponseDto> login(LoginRequestDto login);

    void logout(String token);

    String refreshAccessToken(String refreshToken);

}