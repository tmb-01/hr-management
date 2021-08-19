package uz.pdp.appjwtrealemailauditing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.appjwtrealemailauditing.entity.Role;
import uz.pdp.appjwtrealemailauditing.entity.enums.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {

    Optional<Role> findByName(RoleName name);

}
