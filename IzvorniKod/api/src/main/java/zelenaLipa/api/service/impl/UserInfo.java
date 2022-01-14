package zelenaLipa.api.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import zelenaLipa.api.service.UserInfoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserInfo implements UserInfoService {

    @Override
    public String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if(principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = "anonymous";
        }
        return username;
    }

    @Override
    public String getUserRole() {
        List<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(role -> role.getAuthority()).collect(Collectors.toList());
        return roles.get(0);
    }


}
