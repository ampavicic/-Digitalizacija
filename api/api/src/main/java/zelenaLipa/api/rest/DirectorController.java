package zelenaLipa.api.rest;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/director")
public class DirectorController {

    @GetMapping("")
    public ModelAndView director() {

        ModelAndView mv = new ModelAndView("director.html");
        checkVariables(mv);

        return mv;

    }

    @GetMapping("/listOfEmployees") //POST treba biti
    public void listOfEmployees() {

    }

    @GetMapping("/listOfRoles")
    public void listOfRoles() {

    }

    @GetMapping("/setSalary")
    public void setSalary() {

    }

    @GetMapping("/addRoleToEmployee")
    public void addRoleToEmployee() {

    }

    @GetMapping("/removeEmployee")
    public void removeEmployee() {

    }

    @GetMapping("/addEmployee")
    public void updateSalary() {

    }

    @GetMapping("/newestRegistrations")
    public void getNewestRegistrations() {

    }

    @GetMapping("/deleteEmployeeAccount")
    public void deleteEmployeeAccount() {

    }

    @GetMapping("/getStatistics")
    public void getStatistics() {

    }

    @GetMapping("/getSignedDocuments")
    public void getSignedDocuments() {

    }

    @GetMapping("/signDocument")
    public void signDocument() {

    }

    private void checkVariables(ModelAndView mv) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            if (authentication instanceof AnonymousAuthenticationToken) mv.addObject("loggedIn", false);
            else mv.addObject("loggedIn", true);

            if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_DIRECTOR")))
                mv.addObject("roleDirector", true);
            else mv.addObject("roleDirector", false);
        } else { mv.addObject("loggedIn", false); mv.addObject("roleDirector", false); }

    }

}
