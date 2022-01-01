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
    public List<DocumentLink> getLinksForAccountant() { return documentRepo.findByReadByReviserTrueOrderByDateOfSubmissionDesc(); }

    @Override
    public List<DocumentLink> getLinksForDirector() { return documentRepo.findBySentToDirectorTrueOrderByDateOfSubmissionDesc(); }

    @Override
    public List<DocumentLink> getLinksForAccountantToBeArchived() { return documentRepo.findBySignedByDirectorTrueOrderByDateOfSubmissionDesc(); }

    @Override
    public List<DocumentLink> getArchivedLinksForAccountant() { return documentRepo.findByArchivedByAccountantTrueOrderByDateOfSubmissionDesc(); }

}
