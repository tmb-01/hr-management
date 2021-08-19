package uz.pdp.appjwtrealemailauditing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appjwtrealemailauditing.entity.Role;
import uz.pdp.appjwtrealemailauditing.entity.User;

import java.util.*;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndEmailCode(String email, String emailCode);

    Optional<User> findByEmail(String email);

    List<User> findAllByRolesIn(Collection<Set<Role>> roles);
}
