package am.armeniabank.authservicesrc.service;

import am.armeniabank.authserviceapi.request.LoginRequest;
import am.armeniabank.authserviceapi.request.RefreshTokenRequest;
import am.armeniabank.authserviceapi.response.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest login);

    void logout(RefreshTokenRequest refreshToken);

    String refreshAccessToken(RefreshTokenRequest refreshToken);

}