package am.armeniabank.auditservice.repository;

import am.armeniabank.auditservice.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
}
