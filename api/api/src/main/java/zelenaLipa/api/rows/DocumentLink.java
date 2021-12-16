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

    @Override
    public String toString() {
        return "DocumentLink{" +
                "groupId=" + groupId +
                ", docuId=" + docuId +
                ", title='" + title + '\'' +
                '}';
    }
}
