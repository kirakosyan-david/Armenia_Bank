package am.armeniabank.transactionservicesrc.service;

import am.armeniabank.transactionserviceapi.response.FreezeResponse;
import am.armeniabank.transactionservicesrc.entity.Freeze;
import am.armeniabank.transactionservicesrc.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface FreezeService {

    FreezeResponse createFreeze(Transaction transaction, BigDecimal amount, String reason, String token);

    FreezeResponse releaseFreeze(Freeze freeze, String token);

    void completeFreeze(Freeze freeze);

    Freeze getFreezeByTransaction(UUID transactionId);

    List<FreezeResponse> getActiveFreezesForCurrentUser();
}
