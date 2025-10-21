package am.armeniabank.notificationserviceapi.contract;

import am.armeniabank.notificationserviceapi.constants.ApiConstants;
import am.armeniabank.notificationserviceapi.request.NotificationRequest;
import am.armeniabank.notificationserviceapi.response.NotificationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(ApiConstants.NOTIFICATION_SERVICE_URL)
@Validated
@Tag(name = "Notification API", description = "Notification management API")
public interface NotificationController {

    @Operation(summary = "Create notification for user",
            description = "Creates a new notification for the specified user with the chosen currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "409", description = "Notification already exists for user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    ResponseEntity<NotificationResponse> createNotification(@RequestBody @NotNull NotificationRequest notification);

    @Operation(summary = "Get notification by ID",
            description = "Retrieves the notification details for the specified notification ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @GetMapping
    ResponseEntity<List<NotificationResponse>> getNotificationByUserId();
}
