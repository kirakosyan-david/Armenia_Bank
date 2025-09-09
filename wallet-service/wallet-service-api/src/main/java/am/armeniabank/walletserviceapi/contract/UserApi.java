package am.armeniabank.walletserviceapi.contract;

import am.armeniabank.walletserviceapi.constants.ApiConstants;
import am.armeniabank.walletserviceapi.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@Validated
@Tag(name = "User API", description = "API for retrieving user information")
@FeignClient(name = "auth-service", url = "${auth-service.url}")
public interface UserApi {

    @Operation(
            summary = "Get user by ID",
            description = "Fetches user details (first name, last name, etc.) by user ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID format"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ApiConstants.AUTH_SERVICE_URL + "/{id}")
    UserResponse getUserById(@PathVariable("id") UUID id,
                             @RequestHeader("Authorization") String token);
}
