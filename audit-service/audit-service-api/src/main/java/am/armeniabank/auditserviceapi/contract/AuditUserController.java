package am.armeniabank.auditserviceapi.contract;

import am.armeniabank.armeniabankcommon.constants.ApiConstants;
import am.armeniabank.auditserviceapi.request.AuditUserEventRequest;
import am.armeniabank.auditserviceapi.response.AuditUserEventResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(ApiConstants.AUDIT_USER_SERVICE_URL)
public interface AuditUserController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<AuditUserEventResponse> auditUserSaved(@RequestBody AuditUserEventRequest request);
}
