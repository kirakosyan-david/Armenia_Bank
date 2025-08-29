package am.armeniabank.walletservicesrc.mapper;

import am.armeniabank.walletserviceapi.response.UserResponse;
import am.armeniabank.walletserviceapi.response.WalletResponse;
import am.armeniabank.walletservicesrc.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(source = "wallet.id", target = "id")
    @Mapping(source = "wallet.userId", target = "userId")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    WalletResponse toWalletResponse(Wallet wallet, UserResponse user);

}
