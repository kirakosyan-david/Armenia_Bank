package am.armeniabank.auditservicesrc.repository;

import am.armeniabank.auditservicesrc.entity.AuditUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditUserEventRepository extends JpaRepository<AuditUser, UUID> {
}
