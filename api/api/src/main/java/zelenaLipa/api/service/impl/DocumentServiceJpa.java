package zelenaLipa.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zelenaLipa.api.dao.DocumentRepository;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.service.DocumentService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

@Service
public class DocumentServiceJpa implements DocumentService {

    @Autowired
    private DocumentRepository documentRepo;

    @Override
    public Document getDocumentById(Integer docuId) {
        Optional<Document> documentOptional = documentRepo.findById(docuId);
        return documentOptional.get();
    }

    @Override
    public Document getDocumentByIdAndUsername(Integer docuId, String username) {
        Optional<Document> documentOptional = documentRepo.findByDocuIdAndUsername(docuId, username);
        return documentOptional.get();
    }

    @Override
    public int updateSubmittedByEmployee(Integer docuId) {
        Timestamp dateOfSubmission = new Timestamp(System.currentTimeMillis());
        return documentRepo.updateSubmittedByEmployee(docuId, dateOfSubmission);
    }

    @Override
    public int updateReadByReviser(Integer docuId) {
        return documentRepo.updateReadByReviser(docuId);
    }

    @Override
    public int updateSentToDirector(Integer docuId) {
        return documentRepo.updateSentToDirector(docuId);
    }

    @Override
    public int updateSignedByDirector(Integer docuId) {
        return documentRepo.updateSignedByDirector(docuId);
    }

    @Override
    public int updateArchivedByAccountant(Integer docuId) {
        return documentRepo.updateArchivedByAccountant(docuId);
    }

    @Override
    public boolean existByArchiveId(Integer archiveId) {
        return documentRepo.existsByArchiveId(archiveId);
    }

    @Override
    public boolean existByGroupId(Integer groupId) {
        return documentRepo.existsByGroupId(groupId);
    }

    @Override
    public boolean existByDocuId(Integer docuId) {
        return documentRepo.existsById(docuId);
    }

    @Override
    public int giveArchiveId(Integer docuId, Integer archiveId) {
        return documentRepo.updateArchiveId(docuId, archiveId);
    }

    @Override
    public Document storeDocument(Document document) {
        return documentRepo.save(document);
    }

}
