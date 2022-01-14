package zelenaLipa.api.service;

import zelenaLipa.api.domain.UserAccount;

public interface UserAccountService {

    int insertNewUserAccount(UserAccount userAccount);

    boolean hasAnAccountAlready(String genId);

    int activateAccount(String username);

    int deactivateAccount(String username);

}
