package am.armeniabank.transactionservicesrc.mapper;

import am.armeniabank.transactionserviceapi.response.TransactionResponse;
import am.armeniabank.transactionservicesrc.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionResponse mapToTransactionResponse(Transaction transaction);
}
