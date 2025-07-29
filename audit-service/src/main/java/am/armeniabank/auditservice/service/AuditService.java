package am.armeniabank.auditservice.service;

import am.armeniabank.auditservice.dto.AuditEventDto;
import am.armeniabank.auditservice.entity.AuditEvent;
import reactor.core.publisher.Mono;

public interface AuditService {
    Mono<AuditEvent> saveAuditEvent(AuditEventDto auditEventDto);
}
