package zelenaLipa.api.service;

import org.springframework.data.repository.query.Param;
import zelenaLipa.api.domain.Document;

public interface DocumentService {

    Document getDocumentById(Integer docuId);

    Document getDocumentByIdAndUsername(Integer docuId, String username);

    int updateSubmittedByEmployee(Integer docuId);

    int updateReadByReviser(Integer docuId);

    int updateSentToDirector(Integer docuId);

    int updateSignedByDirector(Integer docuId);

    int updateArchivedByAccountant(Integer docuId);

    boolean existByArchiveId(Integer archiveId);

    boolean existByGroupId(Integer groupId);

    boolean existByDocuId(Integer docuId);

    int giveArchiveId(Integer docuId, Integer archiveId);

    Document storeDocument(Document document);

}
