package am.armeniabank.auditservicesrc.repository;

import am.armeniabank.auditservicesrc.entity.AuditWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditWalletEventRepository extends JpaRepository<AuditWallet, UUID> {
}
