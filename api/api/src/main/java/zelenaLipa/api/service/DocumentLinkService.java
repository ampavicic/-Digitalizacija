package zelenaLipa.api.service;

import zelenaLipa.api.domain.DocumentLink;

import java.util.List;

public interface DocumentLinkService {

    List<DocumentLink> getLinksForEmployeeSubmit(Integer groupId, String usename);

    List<DocumentLink> getLinksForEmployeeHistory(String username);

    List<DocumentLink> getLinksForReviser();

    List<DocumentLink> getLinksForAccountant();

    List<DocumentLink> getLinksForAccountantToBeArchived();

    List<DocumentLink> getLinksForDirector();

    List<DocumentLink> getArchivedLinksForAccountant();

}
