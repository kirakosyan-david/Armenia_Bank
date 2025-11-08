package am.armeniabank.authserviceapi.contract;

import am.armeniabank.armeniabankcommon.constants.ApiConstants;
import am.armeniabank.authserviceapi.request.UserUpdateRequest;
import am.armeniabank.authserviceapi.response.UpdateUserResponse;
import am.armeniabank.authserviceapi.response.UserEmailSearchResponse;
import am.armeniabank.authserviceapi.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

import static am.armeniabank.armeniabankcommon.constants.RoleConstants.ADMIN;
import static am.armeniabank.armeniabankcommon.constants.RoleConstants.USER;

@Validated
@SecurityRequirement(name = "keycloak")
@Tag(name = "User API", description = "User management API")
public interface UserController {

    @Operation(summary = "Search users by email",
            description = "Find users whose email matches the provided query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email parameter"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ApiConstants.AUTH_SERVICE_SEARCH_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<UserEmailSearchResponse> searchEmail(@RequestParam String email);

    @Operation(summary = "Get user by ID",
            description = "Retrieve user details by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ApiConstants.AUTH_SERVICE_USER_BY_ID_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<UserResponse> findUserById(@PathVariable UUID id);

    @Operation(summary = "Update user details",
            description = "Update user information identified by the given user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(ApiConstants.AUTH_SERVICE_USER_UPDATE_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<UpdateUserResponse> updateUser(@PathVariable UUID id, @RequestBody UserUpdateRequest request);

    @Operation(summary = "Delete user",
            description = "Delete user by their unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping(ApiConstants.AUTH_SERVICE_USER_DELETE_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID id);

}
