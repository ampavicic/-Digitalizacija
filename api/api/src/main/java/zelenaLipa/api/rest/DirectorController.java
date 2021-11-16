package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.rowMappers.EmployeeRowMapper;
import zelenaLipa.api.rowMappers.RoleRowMapper;
import zelenaLipa.api.rows.Employee;
import zelenaLipa.api.rows.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/director")
public class DirectorController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("")
    public ModelAndView director() {

        ModelAndView mv = new ModelAndView("director.html");
        checkVariables(mv);

        return mv;

    }

    @GetMapping("/employees")
    public ModelAndView employees() {

        ModelAndView mv = new ModelAndView("employees.html");
        checkVariables(mv);

        String sqlSelectRoles = "SELECT * FROM role;";
        List<Role> roleList = jdbcTemplate.query(sqlSelectRoles, new RoleRowMapper());

        mv.addObject("roles", roleList);
        mv.addObject("result", -1);

        return mv;

    }

    @GetMapping("/employees/{result}")
    public ModelAndView employeesResult(@PathVariable(value="result") int result) {

        ModelAndView mv = new ModelAndView("employees.html");
        checkVariables(mv);

        String sqlSelectRoles = "SELECT * FROM role;";
        List<Role> roleList = jdbcTemplate.query(sqlSelectRoles, new RoleRowMapper());

        mv.addObject("roles", roleList);

        mv.addObject("result", result);

        return mv;

    }

    @PostMapping("/employees/add")
    public RedirectView addEmployees(@RequestParam("pid") String pid,
                                     @RequestParam("name") String name,
                                     @RequestParam("surname") String surname,
                                     @RequestParam("residence") String residence,
                                     @RequestParam("salary") String salary,
                                     @RequestParam("roleid") String roleid) {

        String genIdString;
        boolean equal = true;

        do {
            Random rand = new Random(System.currentTimeMillis());
            genIdString = String.valueOf(rand.nextInt(900000000) + 100000000);
            String sqlSelectRoles = "SELECT genid FROM employee;";
            List<String> listOfGenIds = jdbcTemplate.query(sqlSelectRoles, (rs, rowNum) -> {
                String genId;
                genId = rs.getString("genid");
                return genId;
            });
            if(listOfGenIds.contains(genIdString)) equal = true;
            else equal = false;
        } while(equal);

        String sqlInsertEmployee = "INSERT INTO employee (pid, genid, name, surname, residence, salary, roleid) VALUES ( '"
                + pid + "', '"
                + genIdString + "', '"
                + name + "', '"
                + surname + "', '"
                + residence + "', "
                + salary + ", "
                + roleid + ");";

        int result = jdbcTemplate.update(sqlInsertEmployee);



        if(result >= 0 && result <= 1) {
            System.out.println(result);
            return new RedirectView("/director/employees/" + result);
        }
        else return new RedirectView("/error");

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
