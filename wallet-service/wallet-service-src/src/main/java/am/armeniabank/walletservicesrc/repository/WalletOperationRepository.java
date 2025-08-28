package am.armeniabank.walletservicesrc.repository;

import am.armeniabank.walletservicesrc.entity.WalletOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletOperationRepository extends JpaRepository<WalletOperation, UUID> {
}
