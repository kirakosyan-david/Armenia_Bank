package am.armeniabank.authserviceapi.contract;

import am.armeniabank.armeniabankcommon.constants.ApiConstants;
import am.armeniabank.authserviceapi.request.UserProfileRequest;
import am.armeniabank.authserviceapi.request.UserProfileUpdateRequest;
import am.armeniabank.authserviceapi.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

import static am.armeniabank.armeniabankcommon.constants.RoleConstants.ADMIN;
import static am.armeniabank.armeniabankcommon.constants.RoleConstants.USER;

@Validated
@SecurityRequirement(name = "keycloak")
@Tag(name = "User Profile API", description = "API for managing user profiles")
public interface UserProfileController {

    @Operation(summary = "Save user profile",
            description = "Creates or saves the user profile data for the specified user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile saved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(ApiConstants.AUTH_SERVICE_USER_PROFILE_USER_ID_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<UserProfileResponse> saveUserProfile(@PathVariable("userId") UUID userId,
                                                        @Valid @RequestBody UserProfileRequest requestDto);

    @Operation(summary = "Update user profile",
            description = "Updates the user profile data for the specified user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(ApiConstants.AUTH_SERVICE_USER_PROFILE_UPDATE_USER_ID_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<UserProfileResponse> updateUserProfile(@PathVariable("userId") UUID userId,
                                                          @Valid @RequestBody UserProfileUpdateRequest requestDto);

    @Operation(summary = "Get user profile",
            description = "Retrieves the user profile data for the specified user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "404", description = "User profile not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ApiConstants.AUTH_SERVICE_USER_PROFILE_USER_ID_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable("userId") UUID userId);
}
