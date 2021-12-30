package zelenaLipa.api.rows;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Document {

    @NotNull
    private String content;

    @NotNull
    @Size(max = 255)
    private String title;

    private String type;

    @NotNull
    private String readByReviser;

    @NotNull
    private String archivedByAccountant;

    @NotNull
    private String signedByDirector;

    @NotNull
    private String submittedByEmployee;

    private String dateOfSubmission;

    @NotNull
    private String sentToDirector;

    private int archiveId = -1;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReadByReviser() {
        return readByReviser;
    }

    public void setReadByReviser(String readByReviser) {
        this.readByReviser = readByReviser;
    }

    public String getArchivedByAccountant() {
        return archivedByAccountant;
    }

    public void setArchivedByAccountant(String archivedByAccountant) {
        this.archivedByAccountant = archivedByAccountant;
    }

    public String getSignedByDirector() {
        return signedByDirector;
    }

    public void setSignedByDirector(String signedByDirector) {
        this.signedByDirector = signedByDirector;
    }

    public String getSubmittedByEmployee() {
        return submittedByEmployee;
    }

    public void setSubmittedByEmployee(String submittedByEmployee) {
        this.submittedByEmployee = submittedByEmployee;
    }

    public String getDateOfSubmission() {
        return dateOfSubmission;
    }

    public void setDateOfSubmission(String dateOfSubmission) {
        this.dateOfSubmission = dateOfSubmission;
    }

    public String getSentToDirector() {
        return sentToDirector;
    }

    public void setSentToDirector(String sentToDirector) {
        this.sentToDirector = sentToDirector;
    }

    public int getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(int archiveId) {
        this.archiveId = archiveId;
    }

    @Override
    public String toString() {
        return "Document{" +
                "content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", readByReviser='" + readByReviser + '\'' +
                ", archivedByAccountant='" + archivedByAccountant + '\'' +
                ", signedByDirector='" + signedByDirector + '\'' +
                ", submittedByEmployee='" + submittedByEmployee + '\'' +
                ", dateOfSubmission='" + dateOfSubmission + '\'' +
                ", sentToDirector='" + sentToDirector + '\'' +
                ", archiveId=" + archiveId +
                '}';
    }

}
