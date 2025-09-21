package am.armeniabank.auditservicesrc.mapper;

import am.armeniabank.auditserviceapi.response.AuditTransactionEventResponse;
import am.armeniabank.auditserviceapi.response.AuditWalletEventResponse;
import am.armeniabank.auditservicesrc.entity.AuditTransaction;
import am.armeniabank.auditservicesrc.entity.AuditWallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditTransactionMapper {

    @Mapping(source = "auditTransaction.service", target = "service")
    @Mapping(source = "auditTransaction.fromWalletId", target = "fromWalletId")
    @Mapping(source = "auditTransaction.toWalletId", target = "toWalletId")
    @Mapping(source = "auditTransaction.transactionId", target = "transactionId")
    @Mapping(source = "auditTransaction.eventType", target = "eventType")
    @Mapping(source = "auditTransaction.userId", target = "userId")
    @Mapping(source = "auditTransaction.details", target = "details")
    @Mapping(source = "auditTransaction.createdAt", target = "createdAt")
    AuditTransactionEventResponse toAuditTransaction(AuditTransaction auditTransaction);

}
