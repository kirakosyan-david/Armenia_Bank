package am.armeniabank.auditservicesrc.service;

import am.armeniabank.auditserviceapi.request.AuditWalletEventRequest;
import am.armeniabank.auditserviceapi.response.AuditWalletEventResponse;

public interface AuditWalletService {

    AuditWalletEventResponse saveAuditWalletEvent(AuditWalletEventRequest request);
}
