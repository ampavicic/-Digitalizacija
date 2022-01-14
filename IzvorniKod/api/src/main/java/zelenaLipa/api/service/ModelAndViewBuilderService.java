package zelenaLipa.api.service;

import org.springframework.web.servlet.ModelAndView;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.domain.Employee;

import java.util.List;

public interface ModelAndViewBuilderService {

    void fillNavigation(ModelAndView mv);

    void addLinksToPage(ModelAndView mv, List<DocumentLink> documentLinks, int groupId, int page);

    void addDocumentToPage(ModelAndView mv, Document document, int docuId);

    void addEmployeesToPage(ModelAndView mv, List<Employee> employees, int page);

    void filterList(ModelAndView mv, String filter, int page);

    void fillWithRoles(ModelAndView mv);

}
