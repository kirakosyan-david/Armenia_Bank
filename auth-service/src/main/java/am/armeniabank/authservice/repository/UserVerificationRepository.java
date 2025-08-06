package am.armeniabank.authservice.repository;

import am.armeniabank.authservice.entity.UserVerification;
import am.armeniabank.authservice.entity.emuns.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {

    List<UserVerification> findByStatusAndActive(VerificationStatus status, boolean active);
}
