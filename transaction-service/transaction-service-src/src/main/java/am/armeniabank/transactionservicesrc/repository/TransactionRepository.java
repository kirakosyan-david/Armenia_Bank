package am.armeniabank.transactionservicesrc.repository;

import am.armeniabank.transactionservicesrc.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByFromWalletIdOrToWalletId(UUID walletId, UUID walletId1);
}
