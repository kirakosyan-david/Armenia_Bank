package am.armeniabank.auditservicesrc.controller;

import am.armeniabank.auditserviceapi.contract.AuditUserController;
import am.armeniabank.auditserviceapi.request.AuditUserEventRequest;
import am.armeniabank.auditserviceapi.response.AuditUserEventResponse;
import am.armeniabank.auditservicesrc.service.AuditUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuditUserControllerImpl implements AuditUserController {

    private final AuditUserService auditService;

    @Override
    public ResponseEntity<AuditUserEventResponse> auditUserSaved(AuditUserEventRequest request) {
        AuditUserEventResponse userEventResponse = auditService.saveAuditUserEvent(request);
        return ResponseEntity.ok(userEventResponse);
    }
}
