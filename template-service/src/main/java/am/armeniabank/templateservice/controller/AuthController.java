package am.armeniabank.templateservice.controller;

import am.armeniabank.templateservice.request.LoginRequest;
import am.armeniabank.templateservice.request.UserRegistrationRequest;
import am.armeniabank.templateservice.response.TokenResponse;
import am.armeniabank.templateservice.service.AuthService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/template")
public class AuthController {

    private final RestTemplate restTemplate;
    private final AuthService authService;

    @Value("${gateway.base-url}")
    private String baseURL;

    @Value("${auth-service.url}")
    private String authURL;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new UserRegistrationRequest());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(LoginRequest request, HttpSession session, Model model) {
        try {
            TokenResponse tokenResponse = authService.loginUser(request.getEmail(), request.getPassword());

            session.setAttribute("JWT_TOKEN", tokenResponse.getAccessToken());

            return "redirect:" + baseURL + "/template/home";
        } catch (Exception e) {
            model.addAttribute("error", "Incorrect login or password");
            return "redirect:" + baseURL + "/template/login";
        }
    }

    @PostMapping("/register")
    public String register(UserRegistrationRequest request, Model model) {
        try {
            restTemplate.postForEntity(
                    authURL + "/api/register",
                    request,
                    Void.class
            );
            return "redirect:" + baseURL + "/template/login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration error");
            return "redirect:" + baseURL + "/template/register";
        }
    }
}
