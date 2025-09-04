package am.armeniabank.auditservicesrc.service.impl;

import am.armeniabank.auditserviceapi.request.AuditUserEventRequest;
import am.armeniabank.auditserviceapi.response.AuditUserEventResponse;
import am.armeniabank.auditservicesrc.entity.AuditUser;
import am.armeniabank.auditservicesrc.mapper.AuditUserMapper;
import am.armeniabank.auditservicesrc.repository.AuditUserEventRepository;
import am.armeniabank.auditservicesrc.service.AuditUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditUserServiceImpl implements AuditUserService {

    private final AuditUserEventRepository auditEventRepository;
    private final AuditUserMapper auditUserMapper;

    @Override
    public AuditUserEventResponse saveAuditUserEvent(AuditUserEventRequest request) {
        AuditUser auditEvent = AuditUser.builder()
                .service(request.getService())
                .eventType(request.getEventType())
                .details(request.getDetails())
                .createdAt(request.getCreatedAt())
                .build();
        AuditUser savedAuditUserEvent = auditEventRepository.save(auditEvent);
        return auditUserMapper.toAuditUser(savedAuditUserEvent);

    }
}
