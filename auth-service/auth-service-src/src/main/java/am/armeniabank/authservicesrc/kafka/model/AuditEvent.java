package am.armeniabank.authservicesrc.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditEvent {

    private String service;
    private String eventType;
    private String details;
    private LocalDateTime createdAt;
}
