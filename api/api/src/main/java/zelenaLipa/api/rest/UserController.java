package zelenaLipa.api.rest;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.domain.UserAccount;
import zelenaLipa.api.service.DocumentLinkService;
import zelenaLipa.api.service.DocumentService;
import zelenaLipa.api.service.EmployeeService;
import zelenaLipa.api.service.UserAccountService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class UserController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/")
    public ModelAndView welcomePage() {
        ModelAndView mv = new ModelAndView("user/home.html");
        ConditionChecker.checkVariables(mv);
        return mv;
    }

    @GetMapping("/user")
    public ModelAndView userPage() {
        ModelAndView mv = new ModelAndView("user/user.html");
        ConditionChecker.checkVariables(mv);
        String username = ConditionChecker.checkUsername();
        int result = userAccountService.activateAccount(username);
        mv.addObject("username", username);
        return mv;
    }

    @PostMapping("/deactivation")
    public ModelAndView deactivation() {
        String username = ConditionChecker.checkUsername();
        int result = userAccountService.deactivateAccount(username);            //Disable account
        SecurityContextHolder.getContext().setAuthentication(null);             //Logout
        ModelAndView mv = new ModelAndView("user/deactivation");
        ConditionChecker.checkVariables(mv);
        return mv;
    }

    @PostMapping("/login{error}")
    public ModelAndView loginError(@RequestParam boolean error) {
        ModelAndView mv = new ModelAndView("user/login.html");
        ConditionChecker.checkVariables(mv);
        mv.addObject("loginError", true);
        return mv;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView("user/login.html");
        ConditionChecker.checkVariables(mv);
        mv.addObject("loginError", false);
        return mv;
    }

    @GetMapping("/register/{error}")
    public ModelAndView registerError(@PathVariable(value="error") int error) {
        ModelAndView mv = new ModelAndView("user/register.html");
        ConditionChecker.checkVariables(mv);
        String string;
        if(error == 0) string = "Error: you are not employed in this company!";
        else string = "Error: you already have an account!";
        mv.addObject("registerError", string);
        return mv;
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView mv = new ModelAndView("user/register.html"); //Vraća stranicu logina (src/main/java/resources)
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

        UserAccount userAccount = new UserAccount();
        userAccount.setGenId(genId);
        userAccount.setEmail(email);
        userAccount.setUsername(username);
        userAccount.setPassword(encodedPassword);
        userAccount.setEnabled(true);
        userAccount.setDeactivated(false);

        if(!employeeService.isWorkingInCompany(genId)) return new RedirectView("/register/0"); //Ako nije vrati pogresku (0 -> nije zaposlen u tvrtci)
        else {
            if(userAccountService.hasAnAccountAlready(genId)) return new RedirectView("/register/1");             //Ako da, onemoguci mu registraciju
            else {                                                                                       //Ako ne, ubaci novi korisnicki racun u bazu podataka

                //int result = databaseQueries.addNewUserAccount(username, encodedPassword, email, genId);
                int result = userAccountService.insertNewUserAccount(userAccount);
                return new RedirectView("/login");
            }
        }

    }

}
