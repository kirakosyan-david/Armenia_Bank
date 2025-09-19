package am.armeniabank.auditservicesrc.repository;

import am.armeniabank.auditservicesrc.entity.AuditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditTransactionEventRepository extends JpaRepository<AuditTransaction, UUID> {
}
