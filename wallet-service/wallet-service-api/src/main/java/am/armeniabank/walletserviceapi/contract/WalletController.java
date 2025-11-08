package am.armeniabank.walletserviceapi.contract;

import am.armeniabank.armeniabankcommon.constants.ApiConstants;
import am.armeniabank.armeniabankcommon.enums.Currency;
import am.armeniabank.walletserviceapi.response.WalletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

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
    @PostMapping(ApiConstants.WALLET_SERVICE_URL)
    ResponseEntity<WalletResponse> createWallet(@RequestParam @NotNull Currency currency);

    @Operation(summary = "Get wallet by ID",
            description = "Retrieves the wallet details for the specified wallet ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @GetMapping(ApiConstants.WALLET_SERVICE_WALLET_ID_URL)
    ResponseEntity<WalletResponse> getWalletById(@PathVariable("walletId") @NotNull UUID walletId);

    @Operation(summary = "Get wallets by User ID",
            description = "Retrieves all wallets for the specified user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallets retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(ApiConstants.WALLET_SERVICE_USER_ID_WALLETS_URL)
    ResponseEntity<List<WalletResponse>> getWalletsByUserId(@PathVariable("userId") @NotNull UUID userId);

    @Operation(summary = "Block wallet",
            description = "Blocks the specified wallet, preventing further operations")
    @PutMapping(ApiConstants.WALLET_SERVICE_WALLET_ID_BLOCK_URL)
    ResponseEntity<WalletResponse> blockWallet(@PathVariable("walletId") @NotNull UUID walletId);

    @Operation(summary = "Unblock wallet",
            description = "Unblocks the specified wallet, allowing operations again")
    @PutMapping(ApiConstants.WALLET_SERVICE_WALLET_ID_UNBLOCK_URL)
    ResponseEntity<WalletResponse> unblockWallet(@PathVariable("walletId") @NotNull UUID walletId);

    @Operation(summary = "Close wallet",
            description = "Closes the specified wallet permanently")
    @PutMapping(ApiConstants.WALLET_SERVICE_WALLET_ID_CLOSE_URL)
    ResponseEntity<WalletResponse> closeWallet(@PathVariable("walletId") @NotNull UUID walletId);
}
