package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
import zelenaLipa.api.conditionCheckers.DatabaseSQL;
import zelenaLipa.api.rows.Document;
import zelenaLipa.api.rows.DocumentLink;

import java.util.List;

@RestController
@RequestMapping("/reviser")
public class ReviserController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("")
    public ModelAndView reviser() {

        ModelAndView mv = new ModelAndView("reviser/reviser.html");
        ConditionChecker.checkVariables(mv);

        return mv;

    }

    /*TEMPLATE*/
    @GetMapping("/inbox/{page}")
    public ModelAndView getDocuments(@PathVariable(value = "page") int page, @RequestParam(value = "message") boolean messageBoolean) {

        ModelAndView mv = new ModelAndView("reviser/reviserInbox.html");
        ConditionChecker.checkVariables(mv);

        List<DocumentLink> documentLinks = DatabaseSQL.getDocumentLinks(DatabaseSQL.Roles.ROLE_REVISER_CHECK_IF_SUBMITTED, true, jdbcTemplate);

        addLinksToPage(mv, documentLinks, page);

        if(messageBoolean) {
            mv.addObject("message", "Submitted to accountant");
            mv.addObject("color", "color: green");
        }

        return mv;

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

        ModelAndView mv = new ModelAndView("reviser/reviserDocument.html");
        ConditionChecker.checkVariables(mv);

        List<Document> documents = DatabaseSQL.getDocument(docuId, null, jdbcTemplate);

        addDocumentToPage(mv, documents, docuId);

        mv.addObject("page", page);
        mv.addObject("docuId", docuId);

        return mv;

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

    @PostMapping("/inbox/{page}/{docuId}")
    public RedirectView reviserSendDocuToAccountant(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {

        int result = DatabaseSQL.updateColumn(docuId, DatabaseSQL.Roles.ROLE_REVISER_UPDATE_READ, false, jdbcTemplate);
        return new RedirectView("/reviser/inbox/" + page + "?message=true");

    }

}