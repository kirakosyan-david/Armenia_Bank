package am.armeniabank.authserviceapi.contract;

import am.armeniabank.armeniabankcommon.constants.ApiConstants;
import am.armeniabank.authserviceapi.request.LoginRequest;
import am.armeniabank.authserviceapi.request.RefreshTokenRequest;
import am.armeniabank.authserviceapi.request.UserRegistrationRequest;
import am.armeniabank.authserviceapi.response.TokenResponse;
import am.armeniabank.authserviceapi.response.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static am.armeniabank.armeniabankcommon.constants.RoleConstants.ADMIN;
import static am.armeniabank.armeniabankcommon.constants.RoleConstants.USER;

@Validated
@SecurityRequirement(name = "keycloak")
@Tag(name = "Auth API", description = "API for authentication and user management")
public interface AuthController {

    @Operation(summary = "User registration",
            description = "Creates a new user based on the provided registration data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(ApiConstants.AUTH_SERVICE_REGISTER_URL)
    ResponseEntity<UserDto> register(@Valid @RequestBody UserRegistrationRequest register);

    @Operation(summary = "User login",
            description = "Authenticates a user based on the provided login credentials and returns an access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication failed"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(ApiConstants.AUTH_SERVICE_LOGIN_URL)
    ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest login);

    @Operation(summary = "User logout",
            description = "Logs out the user by invalidating the provided refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully logged out"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(ApiConstants.AUTH_SERVICE_LOGOUT_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest refreshToken);

    @Operation(summary = "Refresh user token",
            description = "Generates a new access token using the provided refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token successfully refreshed"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(ApiConstants.AUTH_SERVICE_REFRESH_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<String> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshToken);

}
