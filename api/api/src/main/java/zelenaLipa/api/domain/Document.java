package zelenaLipa.api.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Date;

@Entity
public class Document {

    @Id
    @Column(name = "documentid")
    private Integer docuId;

    @NotNull
    @Column(name = "groupid")
    private Integer groupId;

    @NotNull
    @Column(name = "username")
    @Size(min = 2, max = 50)
    private String username;

    @NotNull
    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    @Size(max = 10)
    private String type;

    @NotNull
    @Column(name = "submittedbyemployee")
    private Boolean submittedByEmployee = false;

    @Column(name = "dateofsubmission")
    //@Temporal(TemporalType.TIMESTAMP)
    private Timestamp dateOfSubmission;

    @NotNull
    @Column(name = "readbyreviser")
    private Boolean readByReviser = false;

    @NotNull
    @Column(name = "archivedbyaccountant")
    private Boolean archivedByAccountant = false;

    @NotNull
    @Column(name = "signedbydirector")
    private Boolean signedByDirector = false;

    @NotNull
    @Column(name = "senttodirector")
    private Boolean sentToDirector = false;

    @Column(name = "archiveid")
    private Integer archiveId = -1;

    public Integer getDocuId() {
        return docuId;
    }

    public void setDocuId(Integer docuId) {
        this.docuId = docuId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSubmittedByEmployee() {
        return submittedByEmployee;
    }

    public void setSubmittedByEmployee(Boolean submittedByEmployee) {
        this.submittedByEmployee = submittedByEmployee;
    }

    public Timestamp getDateOfSubmission() {
        return dateOfSubmission;
    }

    public void setDateOfSubmission(Timestamp dateOfSubmission) {
        this.dateOfSubmission = dateOfSubmission;
    }

    public Boolean getReadByReviser() {
        return readByReviser;
    }

    public void setReadByReviser(Boolean readByReviser) { this.readByReviser = readByReviser; }

    public Boolean getArchivedByAccountant() {
        return archivedByAccountant;
    }

    public void setArchivedByAccountant(Boolean archivedByAccountant) { this.archivedByAccountant = archivedByAccountant; }

    public Boolean getSignedByDirector() {
        return signedByDirector;
    }

    public void setSignedByDirector(Boolean signedByDirector) {
        this.signedByDirector = signedByDirector;
    }

    public Boolean getSentToDirector() {
        return sentToDirector;
    }

    public void setSentToDirector(Boolean sentToDirector) {
        this.sentToDirector = sentToDirector;
    }

    public Integer getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(Integer archiveId) {
        this.archiveId = archiveId;
    }

    @Override
    public String toString() {
        return "Document{" +
                "docuId=" + docuId +
                ", groupId=" + groupId +
                ", username='" + username + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", submittedByEmployee='" + submittedByEmployee + '\'' +
                ", dateOfSubmission='" + dateOfSubmission + '\'' +
                ", readByReviser='" + readByReviser + '\'' +
                ", archivedByAccountant='" + archivedByAccountant + '\'' +
                ", signedByDirector='" + signedByDirector + '\'' +
                ", sentToDirector='" + sentToDirector + '\'' +
                ", archiveId=" + archiveId +
                '}';
    }

}
