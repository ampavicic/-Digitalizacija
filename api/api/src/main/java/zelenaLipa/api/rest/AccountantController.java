package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;


@RestController
@RequestMapping("/accountant")
public class AccountantController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("")
    public ModelAndView accountant() {
        //TO DO promijeniti ovo u stranicu accountant-a
        ModelAndView mv = new ModelAndView("accountant/accountant.html");
        ConditionChecker.checkVariables(mv);

        return mv;

    }

    @GetMapping("/accountant")
    public ModelAndView accountantPage() {

        ModelAndView mv = new ModelAndView("accountant/accountant.html");
        ConditionChecker.checkVariables(mv);

        String username = ConditionChecker.checkUsername();

        mv.addObject("username", username);

        return mv;
    }
}
