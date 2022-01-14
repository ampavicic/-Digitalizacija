package zelenaLipa.api.domain;

import java.sql.Timestamp;
import java.util.Date;

public interface DocumentLink {

    public Integer getDocuId();

    public void setDocuId(Integer docuId);

    public Integer getGroupId();

    public void setGroupId(Integer groupId);

    public String getUsername();

    public void setUsername(String username);

    public String getTitle();

    public void setTitle(String title);

    public String getType();

    public void setType(String type);

    public Boolean getSubmittedByEmployee();

    public void setSubmittedByEmployee(Boolean submittedByEmployee);

    public Timestamp getDateOfSubmission();

    public void setDateOfSubmission(Timestamp dateOfSubmission);

    public Boolean getReadByReviser();

    public void setReadByReviser(Boolean readByReviser);

    public Boolean getArchivedByAccountant();

    public void setArchivedByAccountant(Boolean archivedByAccountant);

    public Boolean getSignedByDirector();

    public void setSignedByDirector(Boolean signedByDirector);

    public Boolean getSentToDirector();

    public void setSentToDirector(Boolean sentToDirector);

    public Integer getArchiveId();

    public void setArchiveId(Integer archiveId);

}
