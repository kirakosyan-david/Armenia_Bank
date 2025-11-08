package am.armeniabank.authserviceapi.contract;

import am.armeniabank.armeniabankcommon.constants.ApiConstants;
import am.armeniabank.authserviceapi.request.ApproveVerificationRequest;
import am.armeniabank.authserviceapi.request.RejectVerificationRequest;
import am.armeniabank.authserviceapi.request.StartVerificationRequest;
import am.armeniabank.authserviceapi.response.UserVerificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

import static am.armeniabank.armeniabankcommon.constants.RoleConstants.ADMIN;
import static am.armeniabank.armeniabankcommon.constants.RoleConstants.USER;

@Validated
@SecurityRequirement(name = "keycloak")
@Tag(name = "User Verification API", description = "API for user identity verification processes")
public interface UserVerificationController {

    @Operation(summary = "Start verification",
            description = "Initiates the verification process for the specified user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification process started successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(ApiConstants.AUTH_SERVICE_VERIFICATION_START_USER_ID_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<UserVerificationResponse> startVerification(@PathVariable("userId") UUID userId,
                                                               @Valid @RequestBody StartVerificationRequest requestDto);

    @Operation(summary = "Upload verification documents",
            description = "Uploads supporting documents for user verification")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or request"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(value = ApiConstants.AUTH_SERVICE_VERIFICATION_UPDATE_DOC_USER_ID_URL, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({ADMIN, USER})
    ResponseEntity<Map<String, String>> uploadDocuments(@PathVariable("userId") UUID userId,
                                                        @RequestPart("file") MultipartFile file);

    @Operation(summary = "Approve verification",
            description = "Approves the user's verification request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification approved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(ApiConstants.AUTH_SERVICE_VERIFICATION_APPROVE_USER_ID_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<Map<String, String>> approveVerification(@PathVariable("userId") UUID userId,
                                                            @Valid @RequestBody ApproveVerificationRequest requestDto);

    @Operation(summary = "Reject verification",
            description = "Rejects the user's verification request with reason")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification rejected successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(ApiConstants.AUTH_SERVICE_VERIFICATION_REJECT_USER_ID_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<Map<String, String>> rejectVerification(@PathVariable("userId") UUID userId,
                                                           @Valid @RequestBody RejectVerificationRequest requestDto);

    @Operation(summary = "Expire verification",
            description = "Expires the verification process for the specified user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification expired successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(ApiConstants.AUTH_SERVICE_VERIFICATION_EXPIRE_USER_ID_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<Map<String, String>> expireVerification(@PathVariable("userId") UUID userId);

    @Operation(summary = "Get verification status",
            description = "Retrieves the current verification status for the specified user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verification status retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient access rights"),
            @ApiResponse(responseCode = "404", description = "Verification status not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ApiConstants.AUTH_SERVICE_VERIFICATION_USER_ID_URL)
    @Secured({ADMIN, USER})
    ResponseEntity<UserVerificationResponse> getVerificationStatus(@PathVariable("userId") UUID userId);

}
