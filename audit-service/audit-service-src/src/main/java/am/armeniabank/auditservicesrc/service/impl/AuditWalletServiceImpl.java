package am.armeniabank.auditservicesrc.service.impl;

import am.armeniabank.auditserviceapi.request.AuditWalletEventRequest;
import am.armeniabank.auditserviceapi.response.AuditWalletEventResponse;
import am.armeniabank.auditservicesrc.entity.AuditWallet;
import am.armeniabank.auditservicesrc.mapper.AuditWalletMapper;
import am.armeniabank.auditservicesrc.repository.AuditWalletEventRepository;
import am.armeniabank.auditservicesrc.service.AuditWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditWalletServiceImpl implements AuditWalletService {

    private final AuditWalletEventRepository auditWalletEventRepository;
    private final AuditWalletMapper auditWalletMapper;

    @Override
    public AuditWalletEventResponse saveAuditWalletEvent(AuditWalletEventRequest request) {
        AuditWallet wallet = AuditWallet.builder()
                .service(request.getService())
                .walletId(request.getWalletId())
                .eventType(request.getEventType())
                .userId(request.getUserId())
                .details(request.getDetails())
                .createdAt(request.getCreatedAt())
                .build();

        AuditWallet auditWalletSaved = auditWalletEventRepository.save(wallet);

        return auditWalletMapper.toAuditWallet(auditWalletSaved);
    }
}
