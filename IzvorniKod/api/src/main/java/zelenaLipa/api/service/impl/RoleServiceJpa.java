package zelenaLipa.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zelenaLipa.api.dao.RoleRepository;
import zelenaLipa.api.domain.Role;
import zelenaLipa.api.service.RoleService;

import java.util.List;

@Service
public class RoleServiceJpa implements RoleService {

    @Autowired
    private RoleRepository roleRepo;

    @Override
    public List<Role> getAll() {
        return roleRepo.findAll();
    }
}
