package zelenaLipa.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.service.DocumentLinkService;
import zelenaLipa.api.service.DocumentService;
import zelenaLipa.api.service.ModelAndViewBuilderService;

import java.util.List;

@RestController
@RequestMapping("/reviser")
public class ReviserController {

    @Autowired
    private DocumentLinkService documentLinkService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ModelAndViewBuilderService modelAndViewBuilderService;

    @GetMapping("")
    public ModelAndView reviser() {
        ModelAndView mv = new ModelAndView("reviser/reviser.html");
        modelAndViewBuilderService.fillNavigation(mv);
        return mv;
    }

    @GetMapping("/inbox/{page}")
    public ModelAndView getDocuments(@PathVariable(value = "page") int page, @RequestParam(value = "message") boolean messageBoolean) {
        ModelAndView mv = new ModelAndView("reviser/reviserInbox.html");
        modelAndViewBuilderService.fillNavigation(mv);
        List<DocumentLink> documentLinks = documentLinkService.getLinksForReviser();
        modelAndViewBuilderService.addLinksToPage(mv, documentLinks, 0, page);
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

    @GetMapping("/inbox/{page}/{docuId}")
    public ModelAndView getDocument(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        ModelAndView mv = new ModelAndView("reviser/reviserDocument.html");
        modelAndViewBuilderService.fillNavigation(mv);
        Document documents = documentService.getDocumentById(docuId);
        modelAndViewBuilderService.addDocumentToPage(mv, documents, docuId);
        mv.addObject("page", page);
        mv.addObject("docuId", docuId);
        return mv;
    }

    @PostMapping("/inbox/{page}/{docuId}")
    public RedirectView reviserSendDocuToAccountant(@PathVariable(value = "page") int page, @PathVariable(value = "docuId") int docuId) {
        int result = documentService.updateReadByReviser(docuId);
        return new RedirectView("/reviser/inbox/" + page + "?message=true");
    }

}