package am.armeniabank.auditservicesrc.service.impl;

import am.armeniabank.auditserviceapi.request.AuditTransactionEventRequest;
import am.armeniabank.auditserviceapi.request.AuditWalletEventRequest;
import am.armeniabank.auditserviceapi.response.AuditTransactionEventResponse;
import am.armeniabank.auditserviceapi.response.AuditWalletEventResponse;
import am.armeniabank.auditservicesrc.entity.AuditTransaction;
import am.armeniabank.auditservicesrc.entity.AuditWallet;
import am.armeniabank.auditservicesrc.mapper.AuditTransactionMapper;
import am.armeniabank.auditservicesrc.mapper.AuditWalletMapper;
import am.armeniabank.auditservicesrc.repository.AuditTransactionEventRepository;
import am.armeniabank.auditservicesrc.repository.AuditWalletEventRepository;
import am.armeniabank.auditservicesrc.service.AuditTransactionService;
import am.armeniabank.auditservicesrc.service.AuditWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditTransactionServiceImpl implements AuditTransactionService {
    private final AuditTransactionEventRepository auditTransactionEventRepository;
    private final AuditTransactionMapper auditTransactionMapper;

    @Override
    public AuditTransactionEventResponse saveAuditTransactionEvent(AuditTransactionEventRequest request) {
        AuditTransaction transaction = AuditTransaction.builder()
                .service(request.getService())
                .fromWalletId(request.getFromWalletId())
                .toWalletId(request.getToWalletId())
                .transactionId(request.getTransactionId())
                .eventType(request.getEventType())
                .userId(request.getUserId())
                .details(request.getDetails())
                .createdAt(request.getCreatedAt())
                .build();

        AuditTransaction auditTransactionSaved = auditTransactionEventRepository.save(transaction);

        return auditTransactionMapper.toAuditTransaction(auditTransactionSaved);
    }
}
