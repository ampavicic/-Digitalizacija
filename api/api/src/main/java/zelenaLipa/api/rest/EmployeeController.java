package zelenaLipa.api.rest;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
import zelenaLipa.api.rowMappers.DocumentLinkRowMapper;
import zelenaLipa.api.rows.DocumentLink;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("")
    public ModelAndView employee() {

        ModelAndView mv = new ModelAndView("employee.html");
        ConditionChecker.checkVariables(mv);

        return mv;

    }

    @GetMapping("/upload")
    public ModelAndView employeeUpload() {

        ModelAndView mv = new ModelAndView("employeeUpload.html");
        ConditionChecker.checkVariables(mv);

        return mv;

    }

    @GetMapping("/upload/submit/result/{groupId}")
    public ModelAndView employeeShowResult(@PathVariable(value="groupId") int groupId) {

        ModelAndView mv = new ModelAndView("employeeUploadResult.html");
        ConditionChecker.checkVariables(mv);

        List<DocumentLink> documentLinks = pullLinksFromDB(groupId);

        addLinksToPage(mv, documentLinks);

        return mv;

    }

    public void addLinksToPage(ModelAndView mv, List<DocumentLink> documentLinks) {




    }

    public List<DocumentLink> pullLinksFromDB(int groupId) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) username = ((UserDetails) principal).getUsername();
        else username = "anonymous";

        String sqlSelectDocumentInfo = "SELECT groupid, documentid, title FROM document WHERE username = '" + username + "' AND groupid = " + groupId + ";";

        documentLinks = jdbcTemplate.query(sqlSelectDocumentInfo, new DocumentLinkRowMapper());

        return documentLinks;

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

        return new RedirectView("/employee/upload/submit/result/" + groupId);

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

        System.out.println(resultOCR);

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

        String designation;
        int type = 0; //INT 1, P 2, R 3

        Pattern patternINT = Pattern.compile("INT[0-9][0-9][0-9][0-9]");
        Matcher matcher = patternINT.matcher(resultOCR);

        if(matcher.find()) { designation = matcher.group(); type = 1; }
        else {

            Pattern patternP = Pattern.compile("P[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]");
            matcher = patternP.matcher(resultOCR);

            if(matcher.find()) { designation = matcher.group(); type = 2; }
            else {

                Pattern patternR = Pattern.compile("R[0-9][0-9][0-9][0-9][0-9][0-9]");
                matcher = patternR.matcher(resultOCR);

                if(matcher.find()) { designation = matcher.group(); type = 3; }
                else designation = null;

            }

        }

        return insertDocuInDB(resultOCR, designation, type, docuId, groupId, title);

    }

    public int insertDocuInDB(String resultOCR, String designation, int type, int docuId, int groupId, String title) {

        String sqlInsertDocu;
        String sqlInsertType;

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        String resultOCRbase64 = Base64.getEncoder().encodeToString(resultOCR.getBytes(StandardCharsets.UTF_8));

        if (principal instanceof UserDetails) username = ((UserDetails) principal).getUsername();
        else username = "anonymous";

        sqlInsertDocu = "INSERT INTO document (documentid, archived, signature, username, content, groupid, title) VALUES("
                + docuId + ", "
                + 0 + ", "
                + 0 + ", '"
                + username + "', "
                + "decode('" + resultOCRbase64 + "', 'base64'), "
                + groupId + ", '"
                + title + "')";

        sqlInsertType = "INSERT INTO internaldocument (documentid, int) VALUES (" + docuId + ", '" + designation + "');";

        int resultDocu, resultType = 0;
        resultDocu = jdbcTemplate.update(sqlInsertDocu);

        if (type != 0) resultType = jdbcTemplate.update(sqlInsertType);

        return docuId;

    }

}
