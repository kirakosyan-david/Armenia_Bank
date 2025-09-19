package am.armeniabank.auditservicesrc.service;

import am.armeniabank.auditserviceapi.request.AuditTransactionEventRequest;
import am.armeniabank.auditserviceapi.response.AuditTransactionEventResponse;

public interface AuditTransactionService {

    AuditTransactionEventResponse saveAuditTransactionEvent(AuditTransactionEventRequest request);
}
