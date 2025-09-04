package am.armeniabank.auditservicesrc.service;

import am.armeniabank.auditserviceapi.request.AuditUserEventRequest;
import am.armeniabank.auditserviceapi.response.AuditUserEventResponse;

public interface AuditUserService {

    AuditUserEventResponse saveAuditUserEvent(AuditUserEventRequest request);
}
