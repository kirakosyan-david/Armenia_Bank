package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.LoginRequestDto;
import am.armeniabank.authservice.dto.RefreshTokenRequestDto;
import am.armeniabank.authservice.dto.TokenResponseDto;

public interface AuthService {

    TokenResponseDto login(LoginRequestDto login);

    void logout(RefreshTokenRequestDto refreshToken);

    String refreshAccessToken(RefreshTokenRequestDto refreshToken);

}