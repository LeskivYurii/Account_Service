package account.repository;

import account.domain.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, String> {

    boolean existsByName(String name);
    Optional<Group> findByName(String name);
}
