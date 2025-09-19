package am.armeniabank.transactionservicesrc.repository;

import am.armeniabank.transactionservicesrc.entity.Freeze;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FreezeRepository extends JpaRepository<Freeze, UUID> {
}
