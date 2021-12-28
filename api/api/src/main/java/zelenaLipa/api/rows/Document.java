package zelenaLipa.api.rows;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Document {

    @NotNull
    private String content;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull
    private int signature;

    @NotNull
    private int archived;

    private String type;

    public int getArchived() {
        return archived;
    }

    public void setArchived(int archived) {
        this.archived = archived;
    }

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

    public int getSignature() {
        return signature;
    }

    public void setSignature(int signature) {
        this.signature = signature;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Document{" +
                "content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", signature=" + signature +
                ", type='" + type + '\'' +
                '}';
    }

}
