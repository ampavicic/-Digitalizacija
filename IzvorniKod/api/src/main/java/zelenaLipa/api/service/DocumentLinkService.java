package zelenaLipa.api.service;

import zelenaLipa.api.domain.DocumentLink;

import java.util.List;

public interface DocumentLinkService {

    List<DocumentLink> getLinksForEmployeeSubmit(Integer groupId, String usename);

    List<DocumentLink> getLinksForEmployeeHistory(String username);

    List<DocumentLink> getLinksForReviser();

    List<DocumentLink> getLinksForAccountant(String type);

    List<DocumentLink> getLinksForAccountantToBeArchived(String type);

    List<DocumentLink> getLinksForDirector();

    List<DocumentLink> getArchivedLinksForAccountant(String type);

}
