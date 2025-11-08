package am.armeniabank.auditserviceapi.contract;

import am.armeniabank.armeniabankcommon.constants.ApiConstants;
import am.armeniabank.auditserviceapi.request.AuditWalletEventRequest;
import am.armeniabank.auditserviceapi.response.AuditWalletEventResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(ApiConstants.AUDIT_WALLET_SERVICE_URL)
public interface AuditWalletController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<AuditWalletEventResponse> auditWalletSaved(@RequestBody AuditWalletEventRequest request);
}
