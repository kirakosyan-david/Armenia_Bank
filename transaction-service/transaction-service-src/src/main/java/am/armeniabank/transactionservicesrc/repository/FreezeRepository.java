package am.armeniabank.transactionservicesrc.repository;

import am.armeniabank.transactionserviceapi.enums.FreezeStatus;
import am.armeniabank.transactionservicesrc.entity.Freeze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FreezeRepository extends JpaRepository<Freeze, UUID> {

    @Query("SELECT f FROM Freeze f WHERE f.walletId = :walletId AND f.transaction.userId = :userId AND f.freezeStatus = :status")
    List<Freeze> findActiveFreezesByWalletAndUser(@Param("walletId") UUID walletId,
                                                  @Param("userId") UUID userId,
                                                  @Param("status") FreezeStatus status);

    Optional<Freeze> findByTransactionId(UUID transactionId);

    @Query("SELECT f FROM Freeze f WHERE f.transaction.userId = :userId AND f.freezeStatus = :status")
    List<Freeze> findAllByTransactionUserIdAndFreezeStatus(@Param("userId") UUID userId,
                                                           @Param("status") FreezeStatus status);

    Optional<Freeze> findByTransactionIdAndFreezeStatus(UUID id, FreezeStatus freezeStatus);

}
