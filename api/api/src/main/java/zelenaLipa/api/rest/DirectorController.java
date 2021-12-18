package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
import zelenaLipa.api.rowMappers.DocumentLinkRowMapper;
import zelenaLipa.api.rowMappers.EmployeeRowMapper;
import zelenaLipa.api.rowMappers.RoleRowMapper;
import zelenaLipa.api.rows.DocumentLink;
import zelenaLipa.api.rows.Employee;
import zelenaLipa.api.rows.Role;

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
        ConditionChecker.checkVariables(mv);

        return mv;

    }

    @GetMapping("/employees")
    public ModelAndView employees() {

        ModelAndView mv = new ModelAndView("directorEmployees.html");
        ConditionChecker.checkVariables(mv);

        String sqlSelectRoles = "SELECT * FROM role;";
        List<Role> roleList = jdbcTemplate.query(sqlSelectRoles, new RoleRowMapper());

        mv.addObject("roles", roleList);
        mv.addObject("result", -1);

        return mv;

    }

    @GetMapping("/employees/{result}")
    public ModelAndView employeesResult(@PathVariable(value="result") int result) {

        ModelAndView mv = new ModelAndView("directorEmployees.html");
        ConditionChecker.checkVariables(mv);

        String sqlSelectRoles = "SELECT * FROM role;";
        List<Role> roleList = jdbcTemplate.query(sqlSelectRoles, new RoleRowMapper());

        mv.addObject("roles", roleList);

        mv.addObject("result", result);

        mv.addObject("string", "Success: added one employee: genId = [" + result + "]");

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
        boolean exists = false;
        Random rand = new Random(System.currentTimeMillis());
        do {
            genIdString = String.valueOf(rand.nextInt(900000000) + 100000000);
            String sqlExists = "SELECT EXISTS(SELECT genid FROM employee WHERE genid = '" + genIdString + "') AS exists;";
            List<String> result = jdbcTemplate.query(sqlExists, (rs, rowNum) -> { return rs.getString("exists"); });
            if(result.get(0).equals("t")) exists = true;
            else exists = false;
        } while(exists);

        String sqlInsertEmployee = "INSERT INTO employee (pid, genid, name, surname, residence, salary, roleid) VALUES ( '"
                + pid + "', '"
                + genIdString + "', '"
                + name + "', '"
                + surname + "', '"
                + residence + "', "
                + salary + ", "
                + roleid + ");";

        int result = jdbcTemplate.update(sqlInsertEmployee);



        if(result == 0) return new RedirectView("/director/employees/" + result);
        else if(result == 1) return new RedirectView("/director/employees/" + genIdString);
        else return new RedirectView("/error");

    }


    @GetMapping("/allEmployees/{page}")
    public ModelAndView getAllEmployees (@PathVariable(value = "page") int page) {

        ModelAndView mv = new ModelAndView("allEmployees.html");
        ConditionChecker.checkVariables(mv);

        List<Employee> allEmployees = pullAllEmployeesFromDB();

        addAllEmployeesToPage(mv, allEmployees, -1, page);

        return mv;
    }

    public List<Employee> pullAllEmployeesFromDB() {

        List<Employee> allEmployees;

        String sqlSelectEmployeeInfo = "SELECT * FROM employee;";

        allEmployees = jdbcTemplate.query(sqlSelectEmployeeInfo, new EmployeeRowMapper());

        return allEmployees;

    }

    public void addAllEmployeesToPage(ModelAndView mv, List<Employee> allEmployees, int groupId, int page) {

        int pages = allEmployees.size() / 10;
        int extra = allEmployees.size() % 10;
        boolean prevDisabled = false, nextDisabled = false;

        if(extra > 0) pages++; //Ako ima ostatka dodaj još jednu stranicu

        int startIndex = (page - 1) * 10; //10 linkova po stranici
        mv.addObject("allEmployees", allEmployees);
        mv.addObject("startIndex", startIndex);
        mv.addObject("page", page);

        if(page == 1) prevDisabled = true;
        if(page == pages) nextDisabled = true;
        mv.addObject("prevDisabled", prevDisabled);
        mv.addObject("nextDisabled", nextDisabled);
        if(groupId > 0) mv.addObject("groupId", groupId);

    }


}
