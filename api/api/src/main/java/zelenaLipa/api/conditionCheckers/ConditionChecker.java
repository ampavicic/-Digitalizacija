package zelenaLipa.api.conditionCheckers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.ModelAndView;

public class ConditionChecker {

    public static void checkVariables(ModelAndView mv) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication instanceof AnonymousAuthenticationToken) mv.addObject("loggedIn", false);
            else {
                mv.addObject("loggedIn", true);

                if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_DIRECTOR"))) {
                    mv.addObject("roleDirector", true);
                } else if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_EMPLOYEE"))) {
                    mv.addObject("roleEmployee", true);
                } else if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ACCOUNTANT")
                                                                                || role.getAuthority().equals("ROLE_ACCOUNTANT_INT4")
                                                                                || role.getAuthority().equals("ROLE_ACCOUNTANT_R6")
                                                                                || role.getAuthority().equals("ROLE_ACCOUNTANT_P9"))) {
                    mv.addObject("roleAccountant", true);
                } else if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_REVISER"))) {
                    mv.addObject("roleReviser", true);
                } else {
                    mv.addObject("roleDirector", false); //FALSE je i po defaultu
                    mv.addObject("roleEmployee", false);
                    mv.addObject("roleAccountant", false);
                    mv.addObject("roleReviser", false);
                }

            }

        } else mv.addObject("loggedIn", false);

    }

    public static String checkUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if(principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = "anonymous";
        }
        return username;
    }

}
