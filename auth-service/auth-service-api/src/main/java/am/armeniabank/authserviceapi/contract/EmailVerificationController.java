package am.armeniabank.authserviceapi.contract;

import am.armeniabank.authserviceapi.constants.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(ApiConstants.USER_SERVICE_VERIFY_EMAIL_URL)
@SecurityRequirement(name = "keycloak")
@Tag(name = "Email Verification API", description = "API for verifying user email addresses")
public interface EmailVerificationController {

    @Operation(summary = "Verify email",
            description = "Verifies user's email address using the provided token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String token);

    @Operation(summary = "Verify updated email",
            description = "Verifies updated email address using the provided token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/update")
    ResponseEntity<String> verifyUpdateEmail(@RequestParam String email, @RequestParam String token);
}
