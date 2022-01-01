package zelenaLipa.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import zelenaLipa.api.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
