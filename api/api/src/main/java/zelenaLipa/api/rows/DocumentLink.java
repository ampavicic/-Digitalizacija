package zelenaLipa.api.rows;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DocumentLink {

    @NotNull
    private int groupId;

    @NotNull
    private int docuId;

    @NotNull
    @Size(max = 255)
    private String title;

    private String type;

    private String dateOfSubmission;

    private int archiveId = -1;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getDocuId() {
        return docuId;
    }

    public void setDocuId(int docuId) {
        this.docuId = docuId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateOfSubmission() {
        return dateOfSubmission;
    }

    public void setDateOfSubmission(String dateOfSubmission) {
        this.dateOfSubmission = dateOfSubmission;
    }

    public int getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(int archiveId) {
        this.archiveId = archiveId;
    }

    @Override
    public String toString() {
        return "DocumentLink{" +
                "groupId=" + groupId +
                ", docuId=" + docuId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", dateOfSubmission='" + dateOfSubmission + '\'' +
                ", archiveId=" + archiveId +
                '}';
    }

}
