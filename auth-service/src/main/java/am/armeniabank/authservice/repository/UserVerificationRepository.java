package am.armeniabank.authservice.repository;

import am.armeniabank.authservice.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {
}
