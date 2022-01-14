package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.service.DocumentLinkService;
import zelenaLipa.api.service.DocumentService;
import zelenaLipa.api.service.ModelAndViewBuilderService;

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

    @Autowired
    ModelAndViewBuilderService modelAndViewBuilderService;

    @GetMapping("")
    public ModelAndView accountant() {

        ModelAndView mv = new ModelAndView("accountant/accountant.html");
        modelAndViewBuilderService.fillNavigation(mv);

        return mv;

    }

    @GetMapping("/inbox/{page}")
    public ModelAndView getDocuments(@PathVariable(value = "page") int page, @RequestParam(value = "message") boolean messageBoolean) {

        ModelAndView mv = new ModelAndView("accountant/accountantInbox.html");
        modelAndViewBuilderService.fillNavigation(mv);

        String type = accountantGetType();

        List<DocumentLink> documentLinks = documentLinkService.getLinksForAccountant(type);

        modelAndViewBuilderService.addLinksToPage(mv, documentLinks, 0, page);

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

    @GetMapping("/inbox/{page}/{docuId}")
    public ModelAndView getDocument(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {

        ModelAndView mv = new ModelAndView("accountant/accountantDocument.html");
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

        int result = documentService.updateSentToDirector(docuId);
        return new RedirectView("/accountant/inbox/" + page + "?message=true");

    }

    @GetMapping("/archivedSubmit/{page}")
    public ModelAndView getDocumentsForArchiving(@PathVariable(value = "page") int page, @RequestParam(value = "message") boolean messageBoolean) {

        ModelAndView mv = new ModelAndView("accountant/accountantInbox.html");
        modelAndViewBuilderService.fillNavigation(mv);
        String type = accountantGetType();
        List<DocumentLink> documentLinks = documentLinkService.getLinksForAccountantToBeArchived(type);
        modelAndViewBuilderService.addLinksToPage(mv, documentLinks, 0, page);
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
        modelAndViewBuilderService.fillNavigation(mv);
        Document document = documentService.getDocumentById(docuId);
        modelAndViewBuilderService.addDocumentToPage(mv, document, docuId);
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
        modelAndViewBuilderService.fillNavigation(mv);
        String type = accountantGetType();
        List<DocumentLink> documentLinks = documentLinkService.getArchivedLinksForAccountant(type);
        modelAndViewBuilderService.addLinksToPage(mv, documentLinks, 0, page);
        mv.addObject("title", "Archived documents");
        mv.addObject("redirect", "archived");
        return mv;

    }

    @GetMapping("/archived/{page}/{docuId}")
    public ModelAndView getArchivedDocument(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {

        ModelAndView mv = new ModelAndView("accountant/accountantDocument.html");
        modelAndViewBuilderService.fillNavigation(mv);
        Document document = documentService.getDocumentById(docuId);
        modelAndViewBuilderService.addDocumentToPage(mv, document, docuId);
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
