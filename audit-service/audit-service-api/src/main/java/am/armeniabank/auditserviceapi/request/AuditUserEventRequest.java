package am.armeniabank.auditserviceapi.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditUserEventRequest {

    private String service;
    private String eventType;
    private String details;
    private LocalDateTime createdAt;
}
