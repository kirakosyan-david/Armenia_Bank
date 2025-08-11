package am.armeniabank.authservicesrc.repository;

import am.armeniabank.authservicesrc.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

}
