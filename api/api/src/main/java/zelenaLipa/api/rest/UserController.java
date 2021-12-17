package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.rowMappers.EmployeeRowMapper;
import zelenaLipa.api.rowMappers.UserAccountRowMapper;
import zelenaLipa.api.rows.Employee;
import zelenaLipa.api.rows.UserAccount;


import java.util.List;

@RestController
public class UserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public ModelAndView welcomePage() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String currentPrincipalName = authentication.getName();

        System.out.println("Na homepageu sam i: " + currentPrincipalName);

        ModelAndView mv = new ModelAndView("home.html");

        return mv;

    }

    @GetMapping("/user")
    public ModelAndView userPage() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        System.out.println("Na userpageu sam i: " + currentPrincipalName);

        ModelAndView mv = new ModelAndView("user.html");

        return mv;


    }

    @GetMapping("/login")
    public ModelAndView login() {

        return new ModelAndView("login.html");

    }

    @GetMapping("/register")
    public ModelAndView register() {

        System.out.println("Na registeru sam");

        ModelAndView mv = new ModelAndView("register.html"); //Vraća stranicu logina (src/main/java/resources)

        return mv;

    }

    @PostMapping("/register")
    public RedirectView loginSubmit(@RequestParam("uniqueID") String genId,
                                    @RequestParam("email") String email,
                                    @RequestParam("username") String username,
                                    @RequestParam("password") String password) {

        System.out.println("ID: " + genId + " email: " + email + " username: " + username + " password: " + password);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); //Default je 10 rundi sifriranja

        String encodedPassword = passwordEncoder.encode(password);           //Enkriptiraj BCrypt-om zaporku u formu

        String sqlSelectEmployee = "SELECT * FROM employee WHERE genid = '" + genId + "';";             //Provjeri jeli korisnik zaposlen u tvrtci
        List<Employee> employeeList = jdbcTemplate.query(sqlSelectEmployee, new EmployeeRowMapper());

        if(employeeList.size() < 1) return new RedirectView("/error"); //Ako nije vrati pogresku
        else {

            String sqlSelectUserAccount = "SELECT * FROM useraccount WHERE genid = '" + genId + "';";    //Ispitaj jeli zaposlenik vec ima otvoren racun
            List<UserAccount> userAccountList = jdbcTemplate.query(sqlSelectUserAccount, new UserAccountRowMapper());

            if(userAccountList.size() > 1) return new RedirectView("/error");               //Ako da, onemoguci mu registraciju
            else {                                                                                       //Ako ne, ubaci novi korisnicki racun u bazu podataka

                String sqlInsertUserAccount = "INSERT INTO useraccount (username, password, email, genid, enabled) VALUES " +
                        "( '"
                        + username + "', '"
                        + encodedPassword + "', '"
                        + email + "', '"
                        + genId + "', " +
                        " true);";
                int result = jdbcTemplate.update(sqlInsertUserAccount);

                return new RedirectView("/login");

            }
        }

    }

}
