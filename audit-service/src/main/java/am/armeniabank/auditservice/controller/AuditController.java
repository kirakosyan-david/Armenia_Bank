package am.armeniabank.auditservice.controller;

import am.armeniabank.auditservice.dto.AuditEventDto;
import am.armeniabank.auditservice.entity.AuditEvent;
import am.armeniabank.auditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuditEvent> audit(@RequestBody AuditEventDto dto) {
        return auditService.saveAuditEvent(dto);
    }
}
