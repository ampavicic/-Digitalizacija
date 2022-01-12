package zelenaLipa.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.domain.Employee;
import zelenaLipa.api.domain.Role;
import zelenaLipa.api.service.EmployeeService;
import zelenaLipa.api.service.ModelAndViewBuilderService;
import zelenaLipa.api.service.RoleService;

import java.util.List;

@Service
public class ModelAndViewBuilder implements ModelAndViewBuilderService {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    RoleService roleService;

    @Override
    public void fillNavigation(ModelAndView mv) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            if (authentication instanceof AnonymousAuthenticationToken) mv.addObject("loggedIn", false);
            else {
                mv.addObject("loggedIn", true);
                if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_DIRECTOR"))) {
                    mv.addObject("roleDirector", true);
                } else if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ACCOUNTANT")
                                                                                || role.getAuthority().equals("ROLE_ACCOUNTANT_INT4")
                                                                                || role.getAuthority().equals("ROLE_ACCOUNTANT_R6")
                                                                                || role.getAuthority().equals("ROLE_ACCOUNTANT_P9"))) {
                    mv.addObject("roleAccountant", true);
                } else if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_REVISER"))) {
                    mv.addObject("roleReviser", true);
                } else {
                    mv.addObject("roleDirector", false);
                    mv.addObject("roleAccountant", false);
                    mv.addObject("roleReviser", false);
                }
            }
        } else mv.addObject("loggedIn", false);
    }

    @Override
    public void addLinksToPage(ModelAndView mv, List<DocumentLink> documentLinks, int groupId, int page) {
        int pages = documentLinks.size() / 10;
        int extra = documentLinks.size() % 10;
        boolean prevDisabled = false, nextDisabled = false;
        if (extra > 0) pages++;
        if (pages == 0 && extra == 0) page = 0;
        int startIndex = (page - 1) * 10;
        mv.addObject("documentLinks", documentLinks);
        mv.addObject("startIndex", startIndex);
        mv.addObject("page", page);
        if (page == 1 || page == 0) prevDisabled = true;
        if (page == pages || page == 0) nextDisabled = true;
        mv.addObject("prevDisabled", prevDisabled);
        mv.addObject("nextDisabled", nextDisabled);
        if(groupId > 0) mv.addObject("groupId", groupId);
    }

    @Override
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

    @Override
    public void addEmployeesToPage(ModelAndView mv, List<Employee> employees, int page) {
        int pages = employees.size() / 10;
        int extra = employees.size() % 10;
        boolean prevDisabled = false, nextDisabled = false;
        if(extra > 0) pages++; //Ako ima ostatka dodaj još jednu stranicu
        if(pages == 0 && extra == 0) page = 0;
        int startIndex = (page - 1) * 10; //10 zaposlenika po stranici
        mv.addObject("employees", employees);
        mv.addObject("startIndex", startIndex);
        mv.addObject("page", page);
        if(page == 1 || page == 0) prevDisabled = true;
        if(page == pages || page == 0) nextDisabled = true;
        mv.addObject("prevDisabled", prevDisabled);
        mv.addObject("nextDisabled", nextDisabled);
    }

    @Override
    public void filterList(ModelAndView mv, String filter, int page) {
        fillNavigation(mv);
        List<Employee> employees = employeeService.filterEmployee(filter);
        addEmployeesToPage(mv, employees, page);
        mv.addObject("filter", filter);
    }

    @Override
    public void fillWithRoles(ModelAndView mv) {
        List<Role> roleList = roleService.getAll();
        mv.addObject("roles", roleList);
    }



}
