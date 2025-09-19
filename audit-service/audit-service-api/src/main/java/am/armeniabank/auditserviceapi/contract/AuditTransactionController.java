package am.armeniabank.auditserviceapi.contract;

import am.armeniabank.auditserviceapi.constants.ApiConstants;
import am.armeniabank.auditserviceapi.request.AuditTransactionEventRequest;
import am.armeniabank.auditserviceapi.response.AuditTransactionEventResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(ApiConstants.AUDIT_TRANSACTION_SERVICE_URL)
public interface AuditTransactionController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<AuditTransactionEventResponse> auditTransactionSaved(@RequestBody AuditTransactionEventRequest request);
}
