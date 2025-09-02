package am.armeniabank.walletservicesrc.mapper;

import am.armeniabank.walletserviceapi.response.WalletOperationResponse;
import am.armeniabank.walletservicesrc.entity.WalletOperation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletOperationMapper {

    @Mapping(source = "walletOperation.id", target = "id")
    @Mapping(source = "walletOperation.walletOperationType", target = "walletOperationType")
    @Mapping(source = "walletOperation.amount", target = "amount")
    @Mapping(source = "walletOperation.walletOperationReason", target = "walletOperationReason")
    @Mapping(source = "walletOperation.createdAt", target = "createdAt")
    @Mapping(source = "walletOperation.wallet.userId", target = "userId")
    @Mapping(source = "walletOperation.wallet.currency", target = "currency")
    @Mapping(source = "walletOperation.wallet.balance", target = "balance")
    @Mapping(source = "walletOperation.wallet.status", target = "status")
    WalletOperationResponse toWalletOperation(WalletOperation walletOperation);
}
