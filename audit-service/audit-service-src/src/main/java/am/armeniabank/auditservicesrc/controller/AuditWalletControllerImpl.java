package am.armeniabank.auditservicesrc.controller;

import am.armeniabank.auditserviceapi.contract.AuditWalletController;
import am.armeniabank.auditserviceapi.request.AuditWalletEventRequest;
import am.armeniabank.auditserviceapi.response.AuditWalletEventResponse;
import am.armeniabank.auditservicesrc.service.AuditWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuditWalletControllerImpl implements AuditWalletController {

    private final AuditWalletService auditWalletService;

    @Override
    public ResponseEntity<AuditWalletEventResponse> auditWalletSaved(AuditWalletEventRequest request) {
        AuditWalletEventResponse auditWalletEventResponse = auditWalletService.saveAuditWalletEvent(request);
        return ResponseEntity.ok(auditWalletEventResponse);
    }
}
