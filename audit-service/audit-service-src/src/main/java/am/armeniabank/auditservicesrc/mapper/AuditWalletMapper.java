package am.armeniabank.auditservicesrc.mapper;

import am.armeniabank.auditserviceapi.response.AuditWalletEventResponse;
import am.armeniabank.auditservicesrc.entity.AuditWallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditWalletMapper {

    @Mapping(source = "auditWallet.service", target = "service")
    @Mapping(source = "auditWallet.walletId", target = "walletId")
    @Mapping(source = "auditWallet.eventType", target = "eventType")
    @Mapping(source = "auditWallet.userId", target = "userId")
    @Mapping(source = "auditWallet.details", target = "details")
    @Mapping(source = "auditWallet.createdAt", target = "createdAt")
    AuditWalletEventResponse toAuditWallet(AuditWallet auditWallet);

}
