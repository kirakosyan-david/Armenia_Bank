package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Override
    public String login(String email, String password) {
        return "";
    }

    @Override
    public void logout(String token) {

    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        return "";
    }
}
