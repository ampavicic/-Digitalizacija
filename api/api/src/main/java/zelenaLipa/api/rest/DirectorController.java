package zelenaLipa.api.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/director")
public class DirectorController {

    @GetMapping("")
    public ModelAndView directorPage() {

        System.out.println("Kao direktor, na svojoj stranici sam");

        ModelAndView mv = new ModelAndView("directorpage.html");
        return mv;

    }

    @GetMapping("/listOfEmployees") //POST treba biti
    public void listOfEmployees() {

        System.out.println("Kao direktor, na listi zaposlenih sam");

    }

    @GetMapping("/listOfRoles")
    public void listOfRoles() {

        System.out.println("Kao direktor, na listi uloga sam");

    }

    @GetMapping("/setSalary")
    public void setSalary() {

        System.out.println("Kao direktor, postavljam placu zaposleniku");

    }

    @GetMapping("/addRoleToEmployee")
    public void addRoleToEmployee() {

        System.out.println("Kao direktor, postavljam ulogu zaposleniku");

    }

    @GetMapping("/removeEmployee")
    public void removeEmployee() {

        System.out.println("Kao direktor, micem zaposlenika");

    }

    @GetMapping("/addEmployee")
    public void updateSalary() {

        System.out.println("Kao direkor, dodajem zaposlenog");

    }

    @GetMapping("/newestRegistrations")
    public void getNewestRegistrations() {

        System.out.println("Kao direkor, pregledavam najnovije registracije");

    }

    @GetMapping("/deleteEmployeeAccount")
    public void deleteEmployeeAccount() {

        System.out.println("Kao direkor, brisem racun zaposlenika");

    }

    @GetMapping("/getStatistics")
    public void getStatistics() {

        System.out.println("Kao direkor, pregledavam statistiku");

    }

    @GetMapping("/getSignedDocuments")
    public void getSignedDocuments() {

        System.out.println("Kao direkor, pregledavam potpisane dokumente");

    }

    @GetMapping("/signDocument")
    public void signDocument() {

        System.out.println("Kao direkor, potpisujem dokument");

    }

}
