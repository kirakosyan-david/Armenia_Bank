package am.armeniabank.authservice.service;

public interface AuthService {

    String login(String email, String password);

    void logout(String token);

    String refreshAccessToken(String refreshToken);

}