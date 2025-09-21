package am.armeniabank.transactionserviceapi.contract;

import am.armeniabank.transactionserviceapi.constants.ApiConstants;
import am.armeniabank.transactionserviceapi.request.WalletOperationRequest;
import am.armeniabank.transactionserviceapi.response.WalletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigDecimal;
import java.util.UUID;


@Tag(name = "Wallet API", description = "API for managing wallet balances")
@FeignClient(name = "wallet-service", url = "${wallet-service.url}")
@Validated
public interface WalletApi {

    @Operation(
            summary = "Freeze funds in wallet",
            description = "Blocks a specified amount in the user's wallet"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds frozen successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet ID or amount"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @PutMapping(ApiConstants.WALLET_SERVICE_URL + "/operation/{walletId}/freeze")
    void freezeWallet(@PathVariable("walletId") UUID walletId,
                      @Valid @RequestBody WalletOperationRequest reason,
                      @RequestHeader("Authorization") String token);

    @Operation(
            summary = "Unfreeze funds in wallet",
            description = "Blocks a specified amount in the user's wallet"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds unfrozen successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet ID or amount"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @PutMapping(ApiConstants.WALLET_SERVICE_URL + "/operation/{walletId}/unfreeze")
    void unfreezeWallet(@PathVariable("walletId") UUID walletId,
                      @Valid @RequestBody WalletOperationRequest reason,
                      @RequestHeader("Authorization") String token);

    @Operation(summary = "Debit funds from wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds debited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet ID or amount"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @PutMapping(ApiConstants.WALLET_SERVICE_URL + "/operation/{walletId}/debit")
    void debitWallet(@PathVariable("walletId") UUID walletId,
                     @Valid @RequestBody WalletOperationRequest reason,
                     @RequestHeader("Authorization") String token);

    @Operation(summary = "Credit funds to wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds credited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet ID or amount"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @PutMapping(ApiConstants.WALLET_SERVICE_URL + "/operation/{walletId}/credit")
    void creditWallet(@PathVariable("walletId") UUID walletId,
                      @Valid @RequestBody WalletOperationRequest reason,
                      @RequestHeader("Authorization") String token);

    @Operation(summary = "Get wallet info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet info retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @GetMapping(ApiConstants.WALLET_SERVICE_URL+"/{walletId}")
    WalletResponse getWalletInfo(@PathVariable("walletId") UUID walletId,
                                 @RequestHeader("Authorization") String token);
}
