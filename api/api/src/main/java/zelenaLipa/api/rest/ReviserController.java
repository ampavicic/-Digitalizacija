package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.conditionCheckers.ConditionChecker;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.service.DocumentLinkService;
import zelenaLipa.api.service.DocumentService;

import java.util.List;

@RestController
@RequestMapping("/reviser")
public class ReviserController {

    @Autowired
    private DocumentLinkService documentLinkService;

    @Autowired
    private DocumentService documentService;

    @GetMapping("")
    public ModelAndView reviser() {
        ModelAndView mv = new ModelAndView("reviser/reviser.html");
        ConditionChecker.checkVariables(mv);
        return mv;
    }

    @GetMapping("/inbox/{page}")
    public ModelAndView getDocuments(@PathVariable(value = "page") int page, @RequestParam(value = "message") boolean messageBoolean) {
        ModelAndView mv = new ModelAndView("reviser/reviserInbox.html");
        ConditionChecker.checkVariables(mv);
        List<DocumentLink> documentLinks = documentLinkService.getLinksForReviser();
        addLinksToPage(mv, documentLinks, page);
        if(messageBoolean) {
            mv.addObject("message", "Submitted to accountant");
            mv.addObject("color", "color: green");
        }
        return mv;
    }

    @PostMapping("/inbox/{page}")
    public RedirectView redirectToOtherResult(@PathVariable(value = "page") int page) {
        return new RedirectView("/reviser/inbox/" + page + "?message=false");
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
        Document documents = documentService.getDocumentById(docuId);
        addDocumentToPage(mv, documents, docuId);
        mv.addObject("page", page);
        mv.addObject("docuId", docuId);
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
    public RedirectView reviserSendDocuToAccountant(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        int result = documentService.updateReadByReviser(docuId);
        return new RedirectView("/reviser/inbox/" + page + "?message=true");

    }

}