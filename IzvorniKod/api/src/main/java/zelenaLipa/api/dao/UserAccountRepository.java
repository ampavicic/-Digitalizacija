package zelenaLipa.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import zelenaLipa.api.domain.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    boolean existsByGenId(String genId);

    @Transactional
    @Modifying
    @Query(value = "update useraccount set deactivated = true where useraccount.username = ?1", nativeQuery = true)
    int deactivatedTrue(@Param("username") String username);

    @Transactional
    @Modifying
    @Query(value = "update useraccount set deactivated = false where useraccount.username = ?1", nativeQuery = true)
    int deactivatedFalse(@Param("username") String username);

}
