package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.LoginRequestDto;
import am.armeniabank.authservice.dto.TokenResponseDto;

public interface AuthService {

    TokenResponseDto login(LoginRequestDto login);

    void logout(String token);

    String refreshAccessToken(String refreshToken);

}