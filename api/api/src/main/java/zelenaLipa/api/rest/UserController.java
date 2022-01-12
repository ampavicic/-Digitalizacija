package zelenaLipa.api.rest;

import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import zelenaLipa.api.service.*;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.domain.UserAccount;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentLinkService documentLinkService;

    @Autowired
    private OCRService ocrService;

    @Autowired
    private ModelAndViewBuilderService modelAndViewBuilderService;

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("/")
    public ModelAndView welcomePage() {
        ModelAndView mv = new ModelAndView("user/home.html");
        modelAndViewBuilderService.fillNavigation(mv);
        return mv;
    }

    @GetMapping("/user")
    public ModelAndView userPage() {
        ModelAndView mv = new ModelAndView("user/user.html");
        modelAndViewBuilderService.fillNavigation(mv);
        String username = userInfoService.getUsername();
        int result = userAccountService.activateAccount(username);
        mv.addObject("username", username);
        return mv;
    }

    @PostMapping("/user/deactivation")
    public ModelAndView deactivation() {
        String username = userInfoService.getUsername();
        int result = userAccountService.deactivateAccount(username);            //Disable account
        SecurityContextHolder.getContext().setAuthentication(null);             //Logout
        ModelAndView mv = new ModelAndView("user/deactivation");
        modelAndViewBuilderService.fillNavigation(mv);
        return mv;
    }

    @GetMapping("/user/upload")
    public ModelAndView employeeUpload() {
        ModelAndView mv = new ModelAndView("user/upload.html");
        modelAndViewBuilderService.fillNavigation(mv);
        return mv;
    }

    @GetMapping("/user/upload/submit/result/{groupId}/{page}/{docuId}")
    public ModelAndView employeeShowDocu(@PathVariable(value="groupId") int groupId, @PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        ModelAndView mv = new ModelAndView("user/resultDocument.html");
        modelAndViewBuilderService.fillNavigation(mv);
        String username = userInfoService.getUsername();
        Document document = documentService.getDocumentByIdAndUsername(docuId, username);
        modelAndViewBuilderService.addDocumentToPage(mv, document, docuId);
        mv.addObject("groupId", groupId);
        mv.addObject("page", page);
        mv.addObject("docuId", docuId);
        return mv;
    }

    @PostMapping("/user/upload/submit/result/{groupId}/{page}/{docuId}")
    public RedirectView employeeSendDocuToReviser(@PathVariable(value="groupId") int groupId, @PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        String role = userInfoService.getUserRole();
        int result;
        if(role.contains("EMPLOYEE")) result = documentService.updateSubmittedByEmployee(docuId);
        else if(role.contains("DIRECTOR")) {
            result = documentService.updateSubmittedByEmployee(docuId);
            result = documentService.updateReadByReviser(docuId);
            result = documentService.updateSentToDirector(docuId);
            result = documentService.updateSignedByDirector(docuId);
        }
        else if(role.contains("REVISER") || role.contains("ACCOUNTANT")) {
            result = documentService.updateSubmittedByEmployee(docuId);
            result = documentService.updateReadByReviser(docuId);
        }
        return new RedirectView("/user/upload/submit/result/" + groupId + "/" + page + "?message=true");
    }

    @GetMapping("/user/upload/submit/result/{groupId}/{page}")
    public ModelAndView employeeShowResult(@PathVariable(value="groupId") int groupId, @PathVariable(value = "page") int page, @RequestParam(value="message") boolean messageBoolean) {
        ModelAndView mv = new ModelAndView("user/uploadResult.html");
        modelAndViewBuilderService.fillNavigation(mv);
        String username = userInfoService.getUsername();
        List<DocumentLink> documentLinks = documentLinkService.getLinksForEmployeeSubmit(groupId, username);
        modelAndViewBuilderService.addLinksToPage(mv, documentLinks, groupId, page);
        if(messageBoolean) {
            mv.addObject("message", "Document submitted");
            mv.addObject("color", "color: green");
        }
        return mv;
    }

    @PostMapping("/user/upload/submit/result/{groupId}/{page}")
    public RedirectView redirectToOtherResult(@PathVariable(value="groupId") int groupId, @PathVariable(value = "page") int page) {
        return new RedirectView("/user/upload/submit/result/" + groupId + "/" + page + "?message=false");
    }

    @GetMapping("/user/documentHistory/{page}")
    public ModelAndView getDocumentHistory (@PathVariable(value = "page") int page) {
        ModelAndView mv = new ModelAndView("user/historyResult.html");
        modelAndViewBuilderService.fillNavigation(mv);
        String username = userInfoService.getUsername();
        List<DocumentLink> documentLinks = documentLinkService.getLinksForEmployeeHistory(username);
        modelAndViewBuilderService.addLinksToPage(mv, documentLinks, -1, page);
        return mv;
    }

    @GetMapping("/user/documentHistory/{page}/{docuId}")
    public ModelAndView employeeShowHistoryDocu(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        ModelAndView mv = new ModelAndView("user/resultDocument.html");
        modelAndViewBuilderService.fillNavigation(mv);
        String username = userInfoService.getUsername();
        Document document = documentService.getDocumentByIdAndUsername(docuId, username);
        modelAndViewBuilderService.addDocumentToPage(mv, document, docuId);
        return mv;
    }

    @PostMapping("/user/upload/submit")
    public RedirectView employeeUploadSubmit(HttpServletRequest request, @RequestParam("files") MultipartFile[] files) {
        String realServletPath = request.getSession().getServletContext().getRealPath("");
        String ext = "";
        Random rand = new Random(System.currentTimeMillis());
        int groupId = 0;
        do {
            groupId = rand.nextInt(900000) + 100000;
        } while(documentService.existByGroupId(groupId));
        for (MultipartFile mpf : files) {
            int docuId = 0;
            String type = mpf.getContentType();
            if (type.equals("image/png")) ext = "input.png";
            if (type.equals("image/jpeg")) ext = "input.jpeg";
            try {
                docuId = ocrService.doOCR(mpf, realServletPath, groupId);
            } catch (IOException | TesseractException e) {
                e.printStackTrace();
            }
        }
        return new RedirectView("/user/upload/submit/result/" + groupId + "/1?message=false");
    }

    @PostMapping("/login{error}")
    public ModelAndView loginError(@RequestParam boolean error) {
        ModelAndView mv = new ModelAndView("user/login.html");
        modelAndViewBuilderService.fillNavigation(mv);
        mv.addObject("loginError", true);
        return mv;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView("user/login.html");
        modelAndViewBuilderService.fillNavigation(mv);
        mv.addObject("loginError", false);
        return mv;
    }

    @GetMapping("/register/{error}")
    public ModelAndView registerError(@PathVariable(value="error") int error) {
        ModelAndView mv = new ModelAndView("user/register.html");
        modelAndViewBuilderService.fillNavigation(mv);
        String string;
        if(error == 0) string = "Error: you are not employed in this company!";
        else string = "Error: you already have an account!";
        mv.addObject("registerError", string);
        return mv;
    }

    @GetMapping("/register")
    public ModelAndView register() {
        ModelAndView mv = new ModelAndView("user/register.html"); //Vraća stranicu logina (src/main/java/resources)
        modelAndViewBuilderService.fillNavigation(mv);
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
                int result = userAccountService.insertNewUserAccount(userAccount);
                return new RedirectView("/login");
            }
        }

    }

}
