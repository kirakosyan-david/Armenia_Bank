package am.armeniabank.transactionservicesrc.repository;

import am.armeniabank.transactionservicesrc.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionLogRepository extends JpaRepository<TransactionLog, UUID> {
}
