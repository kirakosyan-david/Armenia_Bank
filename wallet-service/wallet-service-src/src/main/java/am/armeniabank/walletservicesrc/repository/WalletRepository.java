package am.armeniabank.walletservicesrc.repository;

import am.armeniabank.walletservicesrc.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findWalletByUserId(UUID userId);

    List<Wallet> findAllByUserId(UUID userId);

}
