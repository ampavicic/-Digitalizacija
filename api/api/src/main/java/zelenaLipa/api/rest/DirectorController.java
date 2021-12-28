package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
import zelenaLipa.api.rowMappers.EmployeeRowMapper;
import zelenaLipa.api.rowMappers.RoleRowMapper;
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

        ModelAndView mv = new ModelAndView("director/director.html");
        ConditionChecker.checkVariables(mv);

        return mv;

    }

    @GetMapping("/employees")
    public ModelAndView employees() {

        ModelAndView mv = new ModelAndView("director/directorEmployees.html");
        ConditionChecker.checkVariables(mv);

        fillWithRoles(mv);

        mv.addObject("result", "");
        mv.addObject("prevDisabled", true);
        mv.addObject("nextDisabled", true);
        mv.addObject("page", 0);

        return mv;

    }

    @PostMapping("/employees/filter/remove")
    public ModelAndView employeeRemoveBySelected(@RequestParam(value="filter") String filter, @RequestParam(value="pids[]", required = false) String[] checkBoxes) {

        ModelAndView mv = new ModelAndView("director/directorEmployees.html");

        if(checkBoxes != null) {
            int result = removeEmployeesFromDB(checkBoxes);
            mv.addObject("message", "Number of employees removed: " + result);
        }

        filterList(mv, filter, 1);
        fillWithRoles(mv);
        return mv;

    }

    public int removeEmployeesFromDB(String[] checkBoxes) {

        String sqlDelete = "DELETE FROM employee WHERE ";

        for(int i = 0; i < checkBoxes.length - 1; i++) {

            sqlDelete += "pid = '" + checkBoxes[i] + "' OR ";

        }

        sqlDelete += "pid = '" + checkBoxes[checkBoxes.length - 1] + "';";
        int result = jdbcTemplate.update(sqlDelete);

        return result;

    }

    @PostMapping("/employees/filter/{page}")
    public ModelAndView employeeFilterGet(@RequestParam(value="filter") String filter, @PathVariable(value="page") int page) {

        ModelAndView mv = new ModelAndView("director/directorEmployees.html");
        filterList(mv, filter, page);
        fillWithRoles(mv);
        return mv;

    }

    public void filterList(ModelAndView mv, String filter, int page) {

        ConditionChecker.checkVariables(mv);

        List<Employee> employees;

        String sqlSelectFilter = "SELECT * FROM employee WHERE name = '" + filter + "' OR surname = '" + filter + "' OR genid = '" + filter + "' OR pid = '" + filter + "';";

        employees = jdbcTemplate.query(sqlSelectFilter, new EmployeeRowMapper());

        addEmployeesToPage(mv, employees, page);

        mv.addObject("filter", filter);

    }

    public void addEmployeesToPage(ModelAndView mv, List<Employee> employees, int page) {

        int pages = employees.size() / 10;
        int extra = employees.size() % 10;
        boolean prevDisabled = false, nextDisabled = false;

        if(extra > 0) pages++; //Ako ima ostatka dodaj još jednu stranicu
        if(pages == 0 && extra == 0) page = 0;

        int startIndex = (page - 1) * 10; //10 zaposlenika po stranici
        mv.addObject("employees", employees);
        mv.addObject("startIndex", startIndex);
        mv.addObject("page", page);

        if(page == 1 || page == 0) prevDisabled = true;
        if(page == pages || page == 0) nextDisabled = true;
        mv.addObject("prevDisabled", prevDisabled);
        mv.addObject("nextDisabled", nextDisabled);

    }

    @GetMapping("/employees/{result}")
    public ModelAndView employeesResult(@PathVariable(value="result") int result) {

        ModelAndView mv = new ModelAndView("director/directorEmployees.html");
        ConditionChecker.checkVariables(mv);

        String sqlSelectRoles = "SELECT * FROM role;";
        List<Role> roleList = jdbcTemplate.query(sqlSelectRoles, new RoleRowMapper());

        mv.addObject("roles", roleList);

        mv.addObject("result", result);

        mv.addObject("string", "Success: added one employee: genId = [" + result + "]");

        return mv;

    }

    @PostMapping("/employees/add")
    public ModelAndView addEmployees(@RequestParam("pid") String pid,
                                     @RequestParam("name") String name,
                                     @RequestParam("surname") String surname,
                                     @RequestParam("residence") String residence,
                                     @RequestParam("salary") String salary,
                                     @RequestParam("roleid") String roleid,
                                     @RequestParam("filter") String filter) {

        ModelAndView mv = new ModelAndView("director/directorEmployees.html");
        ConditionChecker.checkVariables(mv);

        if(pid != null && name != null && surname != null && residence != null && salary != null && roleid != null) {

            String genIdString;
            boolean exists = false;
            Random rand = new Random(System.currentTimeMillis());
            do {
                genIdString = String.valueOf(rand.nextInt(900000000) + 100000000);
                String sqlExists = "SELECT EXISTS(SELECT genid FROM employee WHERE genid = '" + genIdString + "') AS exists;";
                List<String> result = jdbcTemplate.query(sqlExists, (rs, rowNum) -> {
                    return rs.getString("exists");
                });
                if (result.get(0).equals("t")) exists = true;
                else exists = false;
            } while (exists);

            String sqlInsertEmployee = "INSERT INTO employee (pid, genid, name, surname, residence, salary, roleid) VALUES ( '"
                    + pid + "', '"
                    + genIdString + "', '"
                    + name + "', '"
                    + surname + "', '"
                    + residence + "', "
                    + salary + ", "
                    + roleid + ");";

            int result = jdbcTemplate.update(sqlInsertEmployee);

            mv.addObject("message", "Number of employees added: " + result + " [genid = " + genIdString + "]");

        } else {

            mv.addObject("message", "All add employee inputs must have a value!");

        }

        filterList(mv, filter, 1);
        fillWithRoles(mv);



        return mv;

    }

    public void fillWithRoles(ModelAndView mv) {

        String sqlSelectRoles = "SELECT * FROM role;";
        List<Role> roleList = jdbcTemplate.query(sqlSelectRoles, new RoleRowMapper());

        mv.addObject("roles", roleList);

    }

}
