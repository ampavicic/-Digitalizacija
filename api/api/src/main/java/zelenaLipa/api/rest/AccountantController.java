package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.service.DocumentLinkService;
import zelenaLipa.api.service.DocumentService;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accountant")
public class AccountantController {

    @Autowired
    DocumentLinkService documentLinkService;

    @Autowired
    DocumentService documentService;

    @GetMapping("")
    public ModelAndView accountant() {

        ModelAndView mv = new ModelAndView("accountant/accountant.html");
        ConditionChecker.checkVariables(mv);

        return mv;

    }

    @GetMapping("/inbox/{page}")
    public ModelAndView getDocuments(@PathVariable(value = "page") int page, @RequestParam(value = "message") boolean messageBoolean) {

        ModelAndView mv = new ModelAndView("accountant/accountantInbox.html");
        ConditionChecker.checkVariables(mv);

        String type = accountantGetType();

        List<DocumentLink> documentLinks = documentLinkService.getLinksForAccountant(type);

        addLinksToPage(mv, documentLinks, page);

        mv.addObject("title", "Inbox");
        mv.addObject("redirect", "inbox");

        if (messageBoolean) {
            mv.addObject("message", "Sent to director for signup");
            mv.addObject("color", "color: green");
        }

        return mv;

    }

    @PostMapping("/inbox/{page}")
    public RedirectView redirectToOtherResult(@PathVariable(value = "page") int page) {
        return new RedirectView("/accountant/inbox/" + page + "?message=false");
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

        ModelAndView mv = new ModelAndView("accountant/accountantDocument.html");
        ConditionChecker.checkVariables(mv);

        Document document = documentService.getDocumentById(docuId);

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

        if (document.getArchivedByAccountant()) mv.addObject("archived", "Yes");
        else mv.addObject("archived", "No");

        if (document.getSignedByDirector()) mv.addObject("signed", "Yes");
        else mv.addObject("signed", "No");

        if (document.getReadByReviser()) mv.addObject("read", "Yes");
        else mv.addObject("read", "No");

        if (document.getSubmittedByEmployee()) mv.addObject("submitted", "Yes");
        else mv.addObject("submitted", "No");

        mv.addObject("dateOfSubmission", document.getDateOfSubmission());

        if (document.getArchiveId() == -1) mv.addObject("archiveId", "");
        else mv.addObject("archiveId", document.getArchiveId());

    }

    @PostMapping("/inbox/{page}/{docuId}")
    public RedirectView accountantSendDocuToAccountant(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {

        int result = documentService.updateSentToDirector(docuId);
        return new RedirectView("/accountant/inbox/" + page + "?message=true");

    }

    @GetMapping("/archivedSubmit/{page}")
    public ModelAndView getDocumentsForArchiving(@PathVariable(value = "page") int page, @RequestParam(value = "message") boolean messageBoolean) {

        ModelAndView mv = new ModelAndView("accountant/accountantInbox.html");
        ConditionChecker.checkVariables(mv);

        String type = accountantGetType();

        List<DocumentLink> documentLinks = documentLinkService.getLinksForAccountantToBeArchived(type);

        addLinksToPage(mv, documentLinks, page);

        mv.addObject("title", "Archive documents");
        mv.addObject("redirect", "archivedSubmit");

        if (messageBoolean) {
            mv.addObject("message", "Document archived");
            mv.addObject("color", "color: green");
        }

        return mv;

    }

    @PostMapping("/archivedSubmit/{page}")
    public RedirectView redirectToGetDocumentsForArchiving(@PathVariable(value = "page") int page) {
        return new RedirectView("/accountant/archivedSubmit/" + page + "?message=false");
    }

    @GetMapping("/archivedSubmit/{page}/{docuId}")
    public ModelAndView getDocumentForArchiving(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {

        ModelAndView mv = new ModelAndView("accountant/accountantDocument.html");
        ConditionChecker.checkVariables(mv);

        Document document = documentService.getDocumentById(docuId);

        addDocumentToPage(mv, document, docuId);

        mv.addObject("page", page);
        mv.addObject("docuId", docuId);

        mv.addObject("redirect", "archivedSubmit");

        return mv;

    }

    @PostMapping("/archivedSubmit/{page}/{docuId}")
    public RedirectView accountantArchiveDocument(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {

        int result = documentService.updateArchivedByAccountant(docuId);

        Random rand = new Random(System.currentTimeMillis());
        boolean exists = false;
        int archiveId;
        do {
            archiveId = rand.nextInt(900000) + 100000;
            exists = documentService.existByArchiveId(archiveId);
        } while (exists);

        result = documentService.giveArchiveId(docuId, archiveId);
        return new RedirectView("/accountant/archivedSubmit/" + page + "?message=true");

    }

    @GetMapping("/archived/{page}")
    public ModelAndView getArchivedDocuments(@PathVariable(value = "page") int page) {

        ModelAndView mv = new ModelAndView("accountant/accountantInbox.html");
        ConditionChecker.checkVariables(mv);

        String type = accountantGetType();

        List<DocumentLink> documentLinks = documentLinkService.getArchivedLinksForAccountant(type);

        addLinksToPage(mv, documentLinks, page);

        mv.addObject("title", "Archived documents");
        mv.addObject("redirect", "archived");

        return mv;

    }

    @GetMapping("/archived/{page}/{docuId}")
    public ModelAndView getArchivedDocument(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {

        ModelAndView mv = new ModelAndView("accountant/accountantDocument.html");
        ConditionChecker.checkVariables(mv);

        Document document = documentService.getDocumentById(docuId);
        //List<Document> documents = databaseQueries.getDocument(docuId, null);

        addDocumentToPage(mv, document, docuId);

        mv.addObject("page", page);
        mv.addObject("docuId", docuId);

        return mv;

    }

    public String accountantGetType() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentication.getAuthorities().stream().map(r -> r.getAuthority()).collect(Collectors.toList());

        String role = roles.get(0);

        String type;

        if(role.contains("INT4")) type = "INT";
        else if(role.contains("R6")) type = "R";
        else if(role.contains("P9")) type = "P";
        else type = "NO TYPE";
        return type;

    }

}