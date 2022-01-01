package zelenaLipa.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import zelenaLipa.api.domain.Document;
import zelenaLipa.api.domain.DocumentLink;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Integer> {

    List<DocumentLink> findByGroupIdAndUsernameOrderByDateOfSubmissionDesc(Integer groupId, String username);

    List<DocumentLink> findByUsernameOrderByDateOfSubmissionDesc(String username);

    List<DocumentLink> findBySubmittedByEmployeeTrueOrderByDateOfSubmissionDesc();

    List<DocumentLink> findByReadByReviserTrueOrderByDateOfSubmissionDesc();

    List<DocumentLink> findBySentToDirectorTrueOrderByDateOfSubmissionDesc();

    List<DocumentLink> findBySignedByDirectorTrueOrderByDateOfSubmissionDesc();

    List<DocumentLink> findByArchivedByAccountantTrueOrderByDateOfSubmissionDesc();

    Optional<Document> findByDocuIdAndUsername(Integer docuId, String username);

    @Transactional
    @Modifying
    @Query(value = "update document set submittedbyemployee = true, dateofsubmission = ?2 where document.documentid = ?1", nativeQuery = true)
    int updateSubmittedByEmployee(@Param("documentid") Integer docuId, @Param("dateofsubmission") Date timestamp);

    @Transactional
    @Modifying
    @Query(value = "update document set readbyreviser = true where document.documentid = ?1", nativeQuery = true)
    int updateReadByReviser(@Param("documentid") Integer docuId);

    @Transactional
    @Modifying
    @Query(value = "update document set senttodirector = true where document.documentid = ?1", nativeQuery = true)
    int updateSentToDirector(@Param("documentid") Integer docuId);

    @Transactional
    @Modifying
    @Query(value = "update document set signedbydirector = true where document.documentid = ?1", nativeQuery = true)
    int updateSignedByDirector(@Param("documentid") Integer docuId);

    @Transactional
    @Modifying
    @Query(value = "update document set archivedbyaccountant = true where document.documentid = ?1", nativeQuery = true)
    int updateArchivedByAccountant(@Param("documentid") Integer docuId);

    boolean existsByArchiveId(Integer archiveId);

    boolean existsByGroupId(Integer groupId);

    @Transactional
    @Modifying
    @Query(value = "update document set archiveid = ?2 where document.documentid = ?1", nativeQuery = true)
    int updateArchiveId(@Param("documentid") Integer docuId, @Param("archiveid") Integer archiveId);

    Document save(Document document);

}
