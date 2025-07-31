package am.armeniabank.authservice.controller;

import am.armeniabank.authservice.dto.LoginRequestDto;
import am.armeniabank.authservice.dto.TokenResponseDto;
import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.service.AuthService;
import am.armeniabank.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Validated
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public Mono<ResponseEntity<UserDto>> register(@Valid @RequestBody UserRegistrationRequest register){
        return userService.register(register)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build())
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).build()));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponseDto>> login(@RequestBody LoginRequestDto login) {
        return authService.login(login)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build()));
    }
}
