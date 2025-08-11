package am.armeniabank.authservicesrc.repository;

import am.armeniabank.authservicesrc.entity.UserVerification;
import am.armeniabank.authserviceapi.emuns.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {

    List<UserVerification> findByStatusAndActive(VerificationStatus status, boolean active);
}
