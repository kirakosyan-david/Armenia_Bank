package am.armeniabank.auditservicesrc.controller;

import am.armeniabank.auditserviceapi.contract.AuditTransactionController;
import am.armeniabank.auditserviceapi.request.AuditTransactionEventRequest;
import am.armeniabank.auditserviceapi.response.AuditTransactionEventResponse;
import am.armeniabank.auditservicesrc.service.AuditTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuditTransactionControllerImpl implements AuditTransactionController {

    private final AuditTransactionService auditTransactionService;

    @Override
    public ResponseEntity<AuditTransactionEventResponse> auditTransactionSaved(AuditTransactionEventRequest request) {
        AuditTransactionEventResponse auditTransactionEventResponse = auditTransactionService.saveAuditTransactionEvent(request);
        return ResponseEntity.ok(auditTransactionEventResponse);
    }
}
