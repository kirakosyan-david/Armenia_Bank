package am.armeniabank.authserviceapi.response;

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
public class AuditEventResponse {
    private String service;
    private String eventType;
    private String details;
    private LocalDateTime createdAt;
}
