package am.armeniabank.transactionserviceapi.contract;

import am.armeniabank.armeniabankcommon.constants.ApiConstants;
import am.armeniabank.armeniabankcommon.response.ListResponse;
import am.armeniabank.transactionserviceapi.request.TransactionRequest;
import am.armeniabank.transactionserviceapi.response.FreezeResponse;
import am.armeniabank.transactionserviceapi.response.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Validated
@Tag(name = "Transaction API", description = "Transaction management API")
public interface TransactionController {

    @Operation(summary = "Create transaction for user",
            description = "Creates a new transaction for the authenticated user with the chosen currency")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "409", description = "Transaction already exists for user"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(ApiConstants.TRANSACTION_SERVICE_URL)
    ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest request);

    @Operation(summary = "Complete transaction",
            description = "Marks the specified transaction as COMPLETED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(ApiConstants.TRANSACTION_SERVICE_TRANSACTION_ID_COMPLETE_URL)
    ResponseEntity<TransactionResponse> completeTransaction(@PathVariable("transactionId") UUID transactionId);


    @Operation(summary = "Cancel transaction",
            description = "Cancels the specified transaction and rolls back amounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction canceled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(ApiConstants.TRANSACTION_SERVICE_TRANSACTION_ID_CANCEL_URL)
    ResponseEntity<TransactionResponse> cancelTransaction(@PathVariable("transactionId") UUID transactionId);


    @Operation(summary = "Fail transaction",
            description = "Marks the specified transaction as FAILED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction marked as failed"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping(ApiConstants.TRANSACTION_SERVICE_TRANSACTION_ID_FAIL_URL)
    ResponseEntity<TransactionResponse> failTransaction(@PathVariable("transactionId") UUID transactionId);


    @Operation(summary = "Get transaction by ID",
            description = "Retrieves details of a transaction by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ApiConstants.TRANSACTION_SERVICE_TRANSACTION_ID_URL)
    ResponseEntity<TransactionResponse> getTransactionById(@PathVariable("transactionId") UUID transactionId);


    @Operation(summary = "Get transactions by wallet ID",
            description = "Retrieves all transactions associated with the specified wallet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Wallet not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ApiConstants.TRANSACTION_SERVICE_WALLET_WALLET_ID_URL)
    ResponseEntity<List<TransactionResponse>> getTransactionsByWallet(@PathVariable("walletId") UUID walletId);

    @Operation(summary = "Get active freezes for a wallet",
            description = "Retrieves all active freezes associated with the specified wallet for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active freezes retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "No active freezes found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ApiConstants.TRANSACTION_SERVICE_FREEZES_ACTIVE_URL)
    ResponseEntity<ListResponse<FreezeResponse>> getActiveFreezes();
}
