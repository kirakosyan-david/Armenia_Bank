package am.armeniabank.transactionserviceapi.contract;

import am.armeniabank.transactionserviceapi.constants.ApiConstants;
import am.armeniabank.transactionserviceapi.response.WalletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.util.UUID;


@Tag(name = "Wallet API", description = "API for managing wallet balances")
@FeignClient(name = "wallet-service", url = "${wallet-service.url}")
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
    @PostMapping(ApiConstants.WALLET_SERVICE_URL + "/freeze")
    void freezeWallet(@PathVariable("walletId") UUID walletId,
                      BigDecimal amount);

    @Operation(summary = "Debit funds from wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds debited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet ID or amount"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @PostMapping(ApiConstants.WALLET_SERVICE_URL + "/debit")
    void debitWallet(@PathVariable("walletId") UUID walletId,
                     BigDecimal amount);

    @Operation(summary = "Credit funds to wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funds credited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid wallet ID or amount"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @PostMapping(ApiConstants.WALLET_SERVICE_URL + "/credit")
    void creditWallet(@PathVariable("walletId") UUID walletId,
                      BigDecimal amount);

    @Operation(summary = "Get wallet info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wallet info retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Wallet not found")
    })
    @GetMapping(ApiConstants.WALLET_SERVICE_URL + "/info")
    WalletResponse getWalletInfo(@PathVariable("walletId") UUID walletId);
}
