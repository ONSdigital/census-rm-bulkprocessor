package uk.gov.ons.census.bulkprocessor.model.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.ons.census.bulkprocessor.model.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);
}
