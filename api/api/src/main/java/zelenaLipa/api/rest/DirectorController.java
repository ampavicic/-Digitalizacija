package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.domain.Employee;
import zelenaLipa.api.domain.Role;
import zelenaLipa.api.service.*;

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
            List<String> pidList = Arrays.asList(checkBoxes);
            int result = employeeService.fireEmployee(pidList);
            //int result = databaseQueries.removeEmployee(checkBoxes);
            mv.addObject("message", "Number of employees removed: " + result);
            mv.addObject("color", "color: red");
        }
        filterList(mv, filter, 1);
        fillWithRoles(mv);
        return mv;
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
        List<Employee> employees = employeeService.filterEmployee(filter);
        //List<Employee> employees = databaseQueries.filterEmployees(filter);
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
        List<Role> roleList = roleService.getAll();
        mv.addObject("roles", roleList);
        mv.addObject("result", result);
        return mv;
    }

    @PostMapping("/employees/add")
    public ModelAndView addEmployees(@RequestParam("pid") String pid,
                                     @RequestParam("name") String name,
                                     @RequestParam("surname") String surname,
                                     @RequestParam("residence") String residence,
                                     @RequestParam("salary") String salary,
                                     @RequestParam("roleid") String roleId,
                                     @RequestParam("filter") String filter) {
        ModelAndView mv = new ModelAndView("director/directorEmployees.html");
        ConditionChecker.checkVariables(mv);
        if(pid != null && name != null && surname != null && residence != null) {
            Employee employee = new Employee();
            employee.setPid(pid);
            employee.setName(name);
            employee.setSurname(surname);
            employee.setResidence(residence);
            employee.setSalary(Integer.parseInt(salary));
            employee.setRoleId(Integer.parseInt(roleId));
            String genIdString;
            boolean exists = false;
            Random rand = new Random(System.currentTimeMillis());
            do {
                genIdString = String.valueOf(rand.nextInt(900000000) + 100000000);
            } while (!employeeService.genIdFreeToUse(genIdString));
            employee.setGenId(genIdString);
            System.out.println(genIdString);

            employeeService.hireEmployee(employee);

            mv.addObject("message", "Number of employees added: 1 [genid = " + genIdString + "]");
            mv.addObject("color", "color: green");
        } else {
            mv.addObject("message", "All add employee inputs must have a value!");
            mv.addObject("color", "color: red");
        }
        filterList(mv, filter, 1);
        fillWithRoles(mv);
        return mv;
    }

    public void fillWithRoles(ModelAndView mv) {
        List<Role> roleList = roleService.getAll();
        mv.addObject("roles", roleList);
    }

    @GetMapping("/inbox/{page}")
    public ModelAndView getDocuments(@PathVariable(value = "page") int page, @RequestParam(value = "message") boolean messageBoolean) {
        ModelAndView mv = new ModelAndView("director/directorInbox.html");
        ConditionChecker.checkVariables(mv);
        List<DocumentLink> documentLinks = documentLinkService.getLinksForDirector();
        addLinksToPage(mv, documentLinks, page);
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

    public void addLinksToPage(ModelAndView mv, List<DocumentLink> documentLinks, int page) {
        int pages = documentLinks.size() / 10;
        int extra = documentLinks.size() % 10;
        boolean prevDisabled = false, nextDisabled = false;
        if (extra > 0) pages++; //Ako ima ostatka dodaj još jednu stranicu
        if (pages == 0 && extra == 0) page = 0;
        int startIndex = (page - 1) * 10; //10 zaposlenika po stranici
        mv.addObject("documentLinks", documentLinks);
        mv.addObject("startIndex", startIndex);
        mv.addObject("page", page);
        if (page == 1 || page == 0) prevDisabled = true;
        if (page == pages || page == 0) nextDisabled = true;
        mv.addObject("prevDisabled", prevDisabled);
        mv.addObject("nextDisabled", nextDisabled);
    }

    @GetMapping("/inbox/{page}/{docuId}")
    public ModelAndView getDocument(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        ModelAndView mv = new ModelAndView("director/directorDocument.html");
        ConditionChecker.checkVariables(mv);
        Document document = documentService.getDocumentById(docuId);
        //List<Document> documents = databaseQueries.getDocument(docuId, null);
        addDocumentToPage(mv, document, docuId);
        mv.addObject("page", page);
        mv.addObject("docuId", docuId);
        mv.addObject("redirect", "inbox");
        return mv;
    }

    public void addDocumentToPage(ModelAndView mv, Document document, int docuId) {
        mv.addObject("title", document.getTitle());
        mv.addObject("content", document.getContent());
        mv.addObject("docuId", docuId);
        mv.addObject("type", document.getType());
        if(document.getArchivedByAccountant()) mv.addObject("archived", "Yes");
        else mv.addObject("archived", "No");
        if(document.getSignedByDirector()) mv.addObject("signed", "Yes");
        else mv.addObject("signed", "No");
        if(document.getReadByReviser()) mv.addObject("read", "Yes");
        else mv.addObject("read", "No");
        if(document.getSubmittedByEmployee()) mv.addObject("submitted", "Yes");
        else mv.addObject("submitted", "No");
        mv.addObject("dateOfSubmission", document.getDateOfSubmission());
        if(document.getArchiveId() == -1) mv.addObject("archiveId", "");
        else mv.addObject("archiveId", document.getArchiveId());
    }

    @PostMapping("/inbox/{page}/{docuId}")
    public RedirectView accountantSendDocuToAccountant(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        int result = documentService.updateSignedByDirector(docuId);
        return new RedirectView("/director/inbox/" + page + "?message=true");
    }

}
