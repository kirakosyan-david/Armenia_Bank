package am.armeniabank.auditservice.service.impl;

import am.armeniabank.auditservice.dto.AuditEventDto;
import am.armeniabank.auditservice.entity.AuditEvent;
import am.armeniabank.auditservice.repository.AuditEventRepository;
import am.armeniabank.auditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditEventRepository auditEventRepository;

    @Override
    public Mono<AuditEvent> saveAuditEvent(AuditEventDto auditEventDto) {
        AuditEvent auditEvent = AuditEvent.builder()
                .service(auditEventDto.getService())
                .eventType(auditEventDto.getEventType())
                .details(auditEventDto.getDetails())
                .createdAt(auditEventDto.getCreatedAt())
                .build();
        return Mono.fromCallable(() -> auditEventRepository.save(auditEvent));

    }
}
