package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
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

        ModelAndView mv = new ModelAndView("home.html");
        ConditionChecker.checkVariables(mv);

        return mv;

    }

    @GetMapping("/user")
    public ModelAndView userPage() {

        ModelAndView mv = new ModelAndView("user.html");
        ConditionChecker.checkVariables(mv);

        String username = ConditionChecker.checkUsername();

        mv.addObject("username", username);

        return mv;


    }

    @PostMapping("/login{error}")
    public ModelAndView loginError(@RequestParam boolean error) {

        ModelAndView mv = new ModelAndView("login.html");
        ConditionChecker.checkVariables(mv);
        mv.addObject("loginError", true);

        return mv;

    }

    @GetMapping("/login")
    public ModelAndView login() {

        ModelAndView mv = new ModelAndView("login.html");
        ConditionChecker.checkVariables(mv);
        mv.addObject("loginError", false);

        return mv;

    }

    @GetMapping("/register/{error}")
    public ModelAndView registerError(@PathVariable(value="error") int error) {

        ModelAndView mv = new ModelAndView("register.html");
        ConditionChecker.checkVariables(mv);
        String string;
        if(error == 0) string = "Error: you are not employed in this company!";
        else string = "Error: you already have an account!";
        mv.addObject("registerError", string);
        return mv;

    }

    @GetMapping("/register")
    public ModelAndView register() {

        ModelAndView mv = new ModelAndView("register.html"); //Vraća stranicu logina (src/main/java/resources)
        ConditionChecker.checkVariables(mv);

        return mv;

    }

    @PostMapping("/register")
    public RedirectView loginSubmit(@RequestParam("uniqueID") String genId,
                                    @RequestParam("email") String email,
                                    @RequestParam("username") String username,
                                    @RequestParam("password") String password) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); //Default je 10 rundi sifriranja

        String encodedPassword = passwordEncoder.encode(password);           //Enkriptiraj BCrypt-om zaporku u formu

        String sqlSelectEmployee = "SELECT * FROM employee WHERE genid = '" + genId + "';";             //Provjeri jeli korisnik zaposlen u tvrtci
        List<Employee> employeeList = jdbcTemplate.query(sqlSelectEmployee, new EmployeeRowMapper());

        if(employeeList.size() < 1) return new RedirectView("/register/0"); //Ako nije vrati pogresku (0 -> nije zaposlen u tvrtci)
        else {

            String sqlSelectUserAccount = "SELECT * FROM useraccount WHERE genid = '" + genId + "';";    //Ispitaj jeli zaposlenik vec ima otvoren racun
            List<UserAccount> userAccountList = jdbcTemplate.query(sqlSelectUserAccount, new UserAccountRowMapper());

            if(userAccountList.size() > 0) return new RedirectView("/register/1");             //Ako da, onemoguci mu registraciju
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
