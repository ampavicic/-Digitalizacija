package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.domain.Employee;
import zelenaLipa.api.domain.Role;
import zelenaLipa.api.service.*;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/director")
public class DirectorController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DocumentLinkService documentLinkService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ModelAndViewBuilderService modelAndViewBuilderService;

    @GetMapping("")
    public ModelAndView director() {
        ModelAndView mv = new ModelAndView("director/director.html");
        modelAndViewBuilderService.fillNavigation(mv);
        return mv;
    }

    @GetMapping("/employees")
    public ModelAndView employees() {
        ModelAndView mv = new ModelAndView("director/directorEmployees.html");
        modelAndViewBuilderService.fillNavigation(mv);
        modelAndViewBuilderService.fillWithRoles(mv);
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
            List<String> pidList = Arrays.asList(checkBoxes);
            int result = employeeService.fireEmployee(pidList);
            mv.addObject("message", "Number of employees removed: " + result);
            mv.addObject("color", "color: red");
        }
        modelAndViewBuilderService.filterList(mv, filter, 1);
        modelAndViewBuilderService.fillWithRoles(mv);
        return mv;
    }

    @PostMapping("/employees/filter/{page}")
    public ModelAndView employeeFilterGet(@RequestParam(value="filter") String filter, @PathVariable(value="page") int page) {
        ModelAndView mv = new ModelAndView("director/directorEmployees.html");
        modelAndViewBuilderService.filterList(mv, filter, page);
        modelAndViewBuilderService.fillWithRoles(mv);
        return mv;
    }

    @GetMapping("/employees/{result}")
    public ModelAndView employeesResult(@PathVariable(value="result") int result) {
        ModelAndView mv = new ModelAndView("director/directorEmployees.html");
        modelAndViewBuilderService.fillNavigation(mv);
        List<Role> roleList = roleService.getAll();
        mv.addObject("roles", roleList);
        mv.addObject("result", result);
        return mv;
    }

    @PostMapping("/employees/add")
    public ModelAndView addEmployees(@RequestParam(required = false, value = "pid") String pid,
                                     @RequestParam(required = false, value = "name") String name,
                                     @RequestParam(required = false, value = "surname") String surname,
                                     @RequestParam(required = false, value = "residence") String residence,
                                     @RequestParam(required = false, value = "salary") String salary,
                                     @RequestParam(required = false, value = "roleid") String roleId,
                                     @RequestParam(required = false, value = "filter") String filter) {
        ModelAndView mv = new ModelAndView("director/directorEmployees.html");
        modelAndViewBuilderService.fillNavigation(mv);
        try {
            if (pid != null && name != null && surname != null && residence != null && salary != null && roleId != null) {
                Employee employee = new Employee();
                employee.setPid(pid);
                employee.setName(name);
                employee.setSurname(surname);
                employee.setResidence(residence);
                employee.setSalary(Integer.parseInt(salary));
                employee.setRoleId(Integer.parseInt(roleId));
                String genIdString;
                Random rand = new Random(System.currentTimeMillis());
                do {
                    genIdString = String.valueOf(rand.nextInt(900000000) + 100000000);
                } while (!employeeService.genIdFreeToUse(genIdString));
                employee.setGenId(genIdString);
                employeeService.hireEmployee(employee);
                mv.addObject("message", "Number of employees added: 1 [genid = " + genIdString + "]");
                mv.addObject("color", "color: green");
            } else {
                mv.addObject("message", "Invalid values!");
                mv.addObject("color", "color: red");
            }
        } catch (NumberFormatException | ConstraintViolationException e) {
            mv.addObject("message", "Some inputs aren't valid!");
            mv.addObject("color", "color: red");
        }
        modelAndViewBuilderService.filterList(mv, filter, 1);
        modelAndViewBuilderService.fillWithRoles(mv);
        return mv;
    }



    @GetMapping("/inbox/{page}")
    public ModelAndView getDocuments(@PathVariable(value = "page") int page, @RequestParam(value = "message") boolean messageBoolean) {
        ModelAndView mv = new ModelAndView("director/directorInbox.html");
        modelAndViewBuilderService.fillNavigation(mv);
        List<DocumentLink> documentLinks = documentLinkService.getLinksForDirector();
        modelAndViewBuilderService.addLinksToPage(mv, documentLinks, 0, page);
        mv.addObject("title", "Inbox");
        mv.addObject("redirect", "inbox");
        if(messageBoolean) {
            mv.addObject("message", "Document signed");
            mv.addObject("color", "color: green");
        }
        return mv;
    }

    @PostMapping("/inbox/{page}")
    public RedirectView redirectToOtherResult(@PathVariable(value = "page") int page) {
        return new RedirectView("/director/inbox/" + page + "?message=false");
    }

    @GetMapping("/inbox/{page}/{docuId}")
    public ModelAndView getDocument(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        ModelAndView mv = new ModelAndView("director/directorDocument.html");
        modelAndViewBuilderService.fillNavigation(mv);
        Document document = documentService.getDocumentById(docuId);
        modelAndViewBuilderService.addDocumentToPage(mv, document, docuId);
        mv.addObject("page", page);
        mv.addObject("docuId", docuId);
        mv.addObject("redirect", "inbox");
        return mv;
    }

    @PostMapping("/inbox/{page}/{docuId}")
    public RedirectView accountantSendDocuToAccountant(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        int result = documentService.updateSignedByDirector(docuId);
        return new RedirectView("/director/inbox/" + page + "?message=true");
    }

}
