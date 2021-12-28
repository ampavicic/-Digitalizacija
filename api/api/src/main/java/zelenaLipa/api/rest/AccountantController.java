package zelenaLipa.api.rest;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
import zelenaLipa.api.rowMappers.DocumentLinkRowMapper;
import zelenaLipa.api.rowMappers.DocumentRowMapper;
import zelenaLipa.api.rows.Document;
import zelenaLipa.api.rows.DocumentLink;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    @GetMapping("/upload")
    public ModelAndView accountantUpload() {

        ModelAndView mv = new ModelAndView("accountant/accountantUpload.html");
        ConditionChecker.checkVariables(mv);
        return mv;

    }

    @GetMapping("/upload/submit/result/{groupId}/{page}/{docuId}")
    public ModelAndView accountantShowDocu(@PathVariable(value="groupId") int groupId, @PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {

        ModelAndView mv = new ModelAndView("accountant/accountantResultDocument.html");
        ConditionChecker.checkVariables(mv);

        List<Document> documents = pullDocumentFromDB(docuId);

        addDocumentToPage(mv, documents, docuId);

        return mv;

    }

    public void addDocumentToPage(ModelAndView mv, List<Document> documents, int docuId) {

        Document document = documents.get(0);

        mv.addObject("title", document.getTitle());

        mv.addObject("content", document.getContent());

        mv.addObject("docuId", docuId);
        mv.addObject("type", document.getType());

        if(document.getArchived() > 0) mv.addObject("archived", "Yes");
        else mv.addObject("archived", "No");

        if(document.getSignature() > 0) mv.addObject("signature", "Yes");
        else mv.addObject("signature", "No");

    }

    public List<Document> pullDocumentFromDB(int docuId) {

        String username = ConditionChecker.checkUsername();

        List<Document> documents;

        String sqlSelectDocument = "SELECT content, title, type, archived, signature FROM document WHERE username = '" + username + "' AND documentid = " + docuId + ";";

        documents = jdbcTemplate.query(sqlSelectDocument, new DocumentRowMapper());

        return documents;

    }

    @GetMapping("/upload/submit/result/{groupId}/{page}")
    public ModelAndView accountantShowResult(@PathVariable(value="groupId") int groupId, @PathVariable(value = "page") int page) {

        ModelAndView mv = new ModelAndView("accountant/accountantUploadResult.html");
        ConditionChecker.checkVariables(mv);

        List<DocumentLink> documentLinks = pullLinksFromDB(groupId);

        addLinksToPage(mv, documentLinks, groupId, page);

        return mv;

    }

    public List<DocumentLink> pullAllLinksFromDB() {

        String username = ConditionChecker.checkUsername();

        List<DocumentLink> documentLinks;

        String sqlSelectDocumentInfo = "SELECT groupid, documentid, title, type FROM document WHERE username = '" + username + "';";

        documentLinks = jdbcTemplate.query(sqlSelectDocumentInfo, new DocumentLinkRowMapper());

        return documentLinks;

    }

    public void addLinksToPage(ModelAndView mv, List<DocumentLink> documentLinks, int groupId, int page) {

        int pages = documentLinks.size() / 10;
        int extra = documentLinks.size() % 10;
        boolean prevDisabled = false, nextDisabled = false;

        if(extra > 0) pages++; //Ako ima ostatka dodaj još jednu stranicu

        int startIndex = (page - 1) * 10; //10 linkova po stranici
        mv.addObject("documentLinks", documentLinks);
        mv.addObject("startIndex", startIndex);
        mv.addObject("page", page);

        if(page == 1) prevDisabled = true;
        if(page == pages) nextDisabled = true;
        mv.addObject("prevDisabled", prevDisabled);
        mv.addObject("nextDisabled", nextDisabled);
        if(groupId > 0) mv.addObject("groupId", groupId);

    }
    public List<DocumentLink> pullLinksFromDB(int groupId) {

        String username = ConditionChecker.checkUsername();

        List<DocumentLink> documentLinks;

        String sqlSelectDocumentInfo = "SELECT groupid, documentid, title, type FROM document WHERE username = '" + username + "' AND groupid = " + groupId + ";";

        documentLinks = jdbcTemplate.query(sqlSelectDocumentInfo, new DocumentLinkRowMapper());

        return documentLinks;

    }

    @PostMapping("/upload/submit")
    public RedirectView accountantUploadSubmit(HttpServletRequest request, @RequestParam("files") MultipartFile[] files) {

        String realServletPath = request.getSession().getServletContext().getRealPath("");
        String ext = "";

        Random rand = new Random(System.currentTimeMillis());
        boolean exists = false;
        int groupId = 0;
        do {
            groupId = rand.nextInt(900000) + 100000;
            String sqlExists = "SELECT EXISTS(SELECT documentid FROM document WHERE groupid = " + groupId + ") AS exists;";
            List<String> result = jdbcTemplate.query(sqlExists, (rs, rowNum) -> { return rs.getString("exists"); });
            if(result.get(0).equals("t")) exists = true;
            else exists = false;
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

        return new RedirectView("/accountant/upload/submit/result/" + groupId + "/1");

    }

    public int startOCR(MultipartFile mpf, String path, String ext, int groupId) throws IOException, TesseractException {

        File img = new File(path + "/images");
        img.mkdir(); //Kreiraj direktorij

        File file = new File(path + "/images/" + mpf.getOriginalFilename());

        System.out.println(path + mpf.getOriginalFilename());

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
            String sqlExists = "SELECT EXISTS(SELECT documentid FROM document WHERE documentid = " + docuId + ") AS exists;";
            List<String> result = jdbcTemplate.query(sqlExists, (rs, rowNum) -> { return rs.getString("exists"); });
            if(result.get(0).equals("t")) exists = true;
            else exists = false;
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

        return insertDocuInDB(resultOCR, designation, docuId, groupId, title);

    }

    public int insertDocuInDB(String resultOCR, String designation, int docuId, int groupId, String title) {

        String username = ConditionChecker.checkUsername();
        String sqlInsertDocu = "INSERT INTO document (documentid, archived, signature, username, content, groupid, title, type) VALUES("
                + docuId + ", "
                + 0 + ", "
                + 0 + ", '"
                + username + "', '"
                + resultOCR + "', "
                + groupId + ", '"
                + title + "', '"
                + designation + "')";

        int resultDocu, resultType = 0;
        resultDocu = jdbcTemplate.update(sqlInsertDocu);

        return docuId;

    }
}
