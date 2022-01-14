package zelenaLipa.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zelenaLipa.api.dao.UserAccountRepository;
import zelenaLipa.api.domain.UserAccount;
import zelenaLipa.api.service.UserAccountService;

@Service
public class UserAccountServiceJpa implements UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepo;

    @Override
    public int insertNewUserAccount(UserAccount userAccount) {

        UserAccount newUserAccount = userAccountRepo.save(userAccount);
        return 1;

    }

    @Override
    public boolean hasAnAccountAlready(String genId) {
        return userAccountRepo.existsByGenId(genId);
    }

    @Override
    public int activateAccount(String username) {
        int result = userAccountRepo.deactivatedFalse(username);
        return result;
    }

    @Override
    public int deactivateAccount(String username) {
        int result = userAccountRepo.deactivatedTrue(username);
        return result;
    }
}
