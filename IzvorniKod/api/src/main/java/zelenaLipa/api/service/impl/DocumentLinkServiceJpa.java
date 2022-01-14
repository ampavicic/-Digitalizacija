package zelenaLipa.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zelenaLipa.api.dao.DocumentRepository;
import zelenaLipa.api.domain.DocumentLink;
import zelenaLipa.api.service.DocumentLinkService;

import java.util.List;

@Service
public class DocumentLinkServiceJpa implements DocumentLinkService {

    @Autowired
    private DocumentRepository documentRepo;

    @Override
    public List<DocumentLink> getLinksForEmployeeSubmit(Integer groupId, String usename) { return documentRepo.findByGroupIdAndUsernameOrderByDateOfSubmissionDesc(groupId, usename); }

    @Override
    public List<DocumentLink> getLinksForEmployeeHistory(String username) { return documentRepo.findByUsernameOrderByDateOfSubmissionDesc(username); }

    @Override
    public List<DocumentLink> getLinksForReviser() { return documentRepo.findBySubmittedByEmployeeTrueOrderByDateOfSubmissionDesc(); }

    @Override
    public List<DocumentLink> getLinksForAccountant(String type) { return documentRepo.findByReadByReviserTrueAndTypeContainsOrderByDateOfSubmissionDesc(type); }

    @Override
    public List<DocumentLink> getLinksForDirector() { return documentRepo.findBySentToDirectorTrueOrderByDateOfSubmissionDesc(); }

    @Override
    public List<DocumentLink> getLinksForAccountantToBeArchived(String type) { return documentRepo.findBySignedByDirectorTrueAndArchivedByAccountantFalseAndTypeContainsOrderByDateOfSubmissionDesc(type); }

    @Override
    public List<DocumentLink> getArchivedLinksForAccountant(String type) { return documentRepo.findByArchivedByAccountantTrueAndTypeContainsOrderByDateOfSubmissionDesc(type); }

}
