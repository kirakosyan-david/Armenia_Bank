package am.armeniabank.auditservicesrc.controller;

import am.armeniabank.auditserviceapi.contract.AuditController;
import am.armeniabank.auditserviceapi.request.AuditUserEventRequest;
import am.armeniabank.auditserviceapi.response.AuditUserEventResponse;
import am.armeniabank.auditservicesrc.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuditControllerImpl implements AuditController {

    private final AuditService auditService;

    @Override
    public ResponseEntity<AuditUserEventResponse> audit(AuditUserEventRequest request) {
        AuditUserEventResponse userEventResponse = auditService.saveAuditEvent(request);
        return ResponseEntity.ok(userEventResponse);
    }
}
