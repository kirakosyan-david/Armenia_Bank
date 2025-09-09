package am.armeniabank.walletservicesrc.repository;

import am.armeniabank.walletserviceapi.enums.Currency;
import am.armeniabank.walletservicesrc.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    List<Wallet> findAllByUserId(UUID userId);

    Optional<Wallet> findByUserIdAndCurrency(UUID userId, Currency currency);

}
