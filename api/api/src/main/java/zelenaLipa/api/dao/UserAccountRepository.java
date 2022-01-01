package zelenaLipa.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import zelenaLipa.api.domain.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    boolean existsByGenId(String genId);

}
