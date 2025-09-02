package am.armeniabank.walletserviceapi.contract;

import am.armeniabank.walletserviceapi.constants.ApiConstants;
import am.armeniabank.walletserviceapi.request.WalletOperationRequest;
import am.armeniabank.walletserviceapi.response.WalletOperationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping(ApiConstants.WALLET_SERVICE_OPERATION_URL)
@Validated
@Tag(name = "Wallet Operation API", description = "Wallet Operation management API")
public interface WalletOperationController {

    @Operation(summary = "Credit wallet balance",
            description = "Credits (adds funds) to the specified wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet credited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{walletId}/credit")
    ResponseEntity<WalletOperationResponse> credit(@PathVariable("walletId") UUID walletId,
                                                   @Valid @RequestBody WalletOperationRequest reason);

    @Operation(summary = "Debit wallet balance",
            description = "Debits (withdraws funds) from the specified wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet debited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{walletId}/debit")
    ResponseEntity<WalletOperationResponse> debit(@PathVariable("walletId") UUID walletId,
                                                  @Valid @RequestBody WalletOperationRequest reason);

    @Operation(summary = "Freeze wallet balance",
            description = "Freezes funds in the specified wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet funds frozen successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{walletId}/freeze")
    ResponseEntity<WalletOperationResponse> freeze(@PathVariable("walletId") UUID walletId,
                                                   @Valid @RequestBody WalletOperationRequest reason);

    @Operation(summary = "Unfreeze wallet balance",
            description = "Unfreezes previously frozen funds in the specified wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet funds unfrozen successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{walletId}/unfreeze")
    ResponseEntity<WalletOperationResponse> unfreeze(@PathVariable("walletId") UUID walletId,
                                                     @Valid @RequestBody WalletOperationRequest reason);

    @Operation(summary = "Get wallet operations",
            description = "Retrieves the list of operations for the specified wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of wallet operations retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{walletId}/operations")
    ResponseEntity<List<WalletOperationResponse>> getOperations(@PathVariable("walletId") UUID walletId);
}
