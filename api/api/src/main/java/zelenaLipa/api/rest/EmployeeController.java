package zelenaLipa.api.rest;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
import zelenaLipa.api.rows.Document;
import zelenaLipa.api.service.DatabaseQueries;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private DatabaseQueries databaseQueries;

    @GetMapping("")
    public ModelAndView employee() {
        ModelAndView mv = new ModelAndView("employee/employee.html");
        ConditionChecker.checkVariables(mv);
        return mv;
    }

    @GetMapping("/upload")
    public ModelAndView employeeUpload() {
        ModelAndView mv = new ModelAndView("employee/employeeUpload.html");
        ConditionChecker.checkVariables(mv);
        return mv;
    }

    @GetMapping("/upload/submit/result/{groupId}/{page}/{docuId}")
    public ModelAndView employeeShowDocu(@PathVariable(value="groupId") int groupId, @PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        ModelAndView mv = new ModelAndView("employee/employeeResultDocument.html");
        ConditionChecker.checkVariables(mv);
        String username = ConditionChecker.checkUsername();
        List<Document> documents = databaseQueries.getDocument(docuId, username);
        addDocumentToPage(mv, documents, docuId);
        mv.addObject("groupId", groupId);
        mv.addObject("page", page);
        mv.addObject("docuId", docuId);
        return mv;
    }

    @PostMapping("/upload/submit/result/{groupId}/{page}/{docuId}")
    public RedirectView employeeSendDocuToReviser(@PathVariable(value="groupId") int groupId, @PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        int result = databaseQueries.updateColumn(docuId, DatabaseQueries.Roles.ROLE_EMPLOYEE_UPDATE_SUBMIT, true);
        return new RedirectView("/employee/upload/submit/result/" + groupId + "/" + page + "?message=true");
    }

    public void addDocumentToPage(ModelAndView mv, List<Document> documents, int docuId) {
        Document document = documents.get(0);
        mv.addObject("title", document.getTitle());
        mv.addObject("content", document.getContent());
        mv.addObject("docuId", docuId);
        mv.addObject("type", document.getType());
        if(document.getArchivedByAccountant().equals("t")) mv.addObject("archived", "Yes");
        else mv.addObject("archived", "No");
        if(document.getSignedByDirector().equals("t")) mv.addObject("signed", "Yes");
        else mv.addObject("signed", "No");
        if(document.getReadByReviser().equals("t")) mv.addObject("read", "Yes");
        else mv.addObject("read", "No");
        if(document.getSubmittedByEmployee().equals("t")) mv.addObject("submitted", "Yes");
        else mv.addObject("submitted", "No");
        mv.addObject("dateOfSubmission", document.getDateOfSubmission());
        if(document.getArchiveId() == -1) mv.addObject("archiveId", "");
        else mv.addObject("archiveId", document.getArchiveId());
    }

    @GetMapping("/upload/submit/result/{groupId}/{page}")
    public ModelAndView employeeShowResult(@PathVariable(value="groupId") int groupId, @PathVariable(value = "page") int page, @RequestParam(value="message") boolean messageBoolean) {
        ModelAndView mv = new ModelAndView("employee/employeeUploadResult.html");
        ConditionChecker.checkVariables(mv);
        String username = ConditionChecker.checkUsername();
        List<Document> documentLinks = databaseQueries.getDocumentLinksByUsernameAndGroupId(groupId, username);
        addLinksToPage(mv, documentLinks, groupId, page);
        if(messageBoolean) {
            mv.addObject("message", "Submitted to reviser");
            mv.addObject("color", "color: green");
        }
        return mv;
    }

    @PostMapping("/upload/submit/result/{groupId}/{page}")
    public RedirectView redirectToOtherResult(@PathVariable(value="groupId") int groupId, @PathVariable(value = "page") int page) {
        return new RedirectView("/employee/upload/submit/result/" + groupId + "/" + page + "?message=false");
    }

    @GetMapping("/documentHistory/{page}")
    public ModelAndView getDocumentHistory (@PathVariable(value = "page") int page) {
        ModelAndView mv = new ModelAndView("employee/employeeHistoryResult.html");
        ConditionChecker.checkVariables(mv);
        String username = ConditionChecker.checkUsername();
        List<Document> documentLinks = databaseQueries.getDocumentLinksByUsername(username);
        addLinksToPage(mv, documentLinks, -1, page);
        return mv;
    }

    @GetMapping("/documentHistory/{page}/{docuId}")
    public ModelAndView employeeShowHistoryDocu(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        ModelAndView mv = new ModelAndView("employee/employeeResultDocument.html");
        ConditionChecker.checkVariables(mv);
        String username = ConditionChecker.checkUsername();
        List<Document> documents = databaseQueries.getDocument(docuId, username);
        addDocumentToPage(mv, documents, docuId);
        return mv;
    }

    public void addLinksToPage(ModelAndView mv, List<Document> documentLinks, int groupId, int page) {
        int pages = documentLinks.size() / 10;
        int extra = documentLinks.size() % 10;
        boolean prevDisabled = false, nextDisabled = false;
        if(extra > 0) pages++; //Ako ima ostatka dodaj još jednu stranicu
        if (pages == 0 && extra == 0) page = 0;
        int startIndex = (page - 1) * 10; //10 linkova po stranici
        mv.addObject("documentLinks", documentLinks);
        mv.addObject("startIndex", startIndex);
        mv.addObject("page", page);
        if (page == 1 || page == 0) prevDisabled = true;
        if (page == pages || page == 0) nextDisabled = true;
        mv.addObject("prevDisabled", prevDisabled);
        mv.addObject("nextDisabled", nextDisabled);
        if(groupId > 0) mv.addObject("groupId", groupId);
    }

    @PostMapping("/upload/submit")
    public RedirectView employeeUploadSubmit(HttpServletRequest request, @RequestParam("files") MultipartFile[] files) {
        String realServletPath = request.getSession().getServletContext().getRealPath("");
        String ext = "";
        Random rand = new Random(System.currentTimeMillis());
        boolean exists = false;
        int groupId = 0;
        do {
            groupId = rand.nextInt(900000) + 100000;
            exists = databaseQueries.existsByGroupId(groupId);
        } while(exists);
        for (MultipartFile mpf : files) {
            int docuId = 0;
            String type = mpf.getContentType();
            if (type.equals("image/png")) ext = "input.png";
            if (type.equals("image/jpeg")) ext = "input.jpeg";
            try {
                docuId = startOCR(mpf, realServletPath, ext, groupId);
            } catch (IOException | TesseractException e) {
                e.printStackTrace();
            }
        }
        return new RedirectView("/employee/upload/submit/result/" + groupId + "/1?message=false");
    }

    public int startOCR(MultipartFile mpf, String path, String ext, int groupId) throws IOException, TesseractException {
        File img = new File(path + "/images");
        img.mkdir(); //Kreiraj direktorij
        File file = new File(path + "/images/" + mpf.getOriginalFilename());
        mpf.transferTo(file);
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("./tessdata");
        tesseract.setLanguage("hrv"); //Set Croatian
        String resultOCR = tesseract.doOCR(file);
        int docuId = storeDocuInDB(resultOCR, groupId, mpf.getOriginalFilename());
        file.delete();
        return docuId;
    }

    public int storeDocuInDB(String resultOCR, int groupId, String title) {
        Random rand = new Random(System.currentTimeMillis());
        boolean exists = false;
        int docuId;
        do {
            docuId = rand.nextInt(900000) + 100000;
            exists = databaseQueries.existsByDocumentId(docuId);
        } while(exists);
        String designation = "";
        Pattern patternINT = Pattern.compile("INT[0-9][0-9][0-9][0-9]");
        Matcher matcher = patternINT.matcher(resultOCR);
        if(matcher.find()) { designation = matcher.group(); }
        else {
            Pattern patternP = Pattern.compile("P[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]");
            matcher = patternP.matcher(resultOCR);
            if(matcher.find()) { designation = matcher.group(); }
            else {
                Pattern patternR = Pattern.compile("R[0-9][0-9][0-9][0-9][0-9][0-9]");
                matcher = patternR.matcher(resultOCR);
                if(matcher.find()) { designation = matcher.group(); }
            }
        }
        String username = ConditionChecker.checkUsername();
        int result = databaseQueries.addDocumentByUsername(docuId, username, resultOCR, groupId, title, designation);
        return result;
    }
}