package am.armeniabank.authservice.repository;

import am.armeniabank.authservice.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(@NotBlank(message = "Email is required")
                          @Email(message = "Incorrect email") String email);
}
