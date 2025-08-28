package am.armeniabank.walletserviceapi.contract;

import am.armeniabank.walletserviceapi.constants.ApiConstants;
import am.armeniabank.walletserviceapi.enums.Currency;
import am.armeniabank.walletserviceapi.response.WalletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RequestMapping(ApiConstants.WALLET_SERVICE_URL)
@Validated
@Tag(name = "Wallet API", description = "Wallet management API")
public interface WalletController {

    @Operation(summary = "Create wallet for user",
            description = "Creates a new wallet for the specified user with the chosen currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "409", description = "Wallet already exists for user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{userId}")
    ResponseEntity<WalletResponse> createWallet(@PathVariable("userId") UUID userId,
                                                @RequestParam Currency currency);

    @Operation(
            summary = "Get wallet by ID",
            description = "Retrieves the wallet details for the specified wallet ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet ID format"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{walletId}")
    ResponseEntity<WalletResponse> getWalletById(@PathVariable("walletId") UUID walletId);
}
