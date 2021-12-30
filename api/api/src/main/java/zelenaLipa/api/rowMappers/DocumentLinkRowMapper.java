package zelenaLipa.api.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import zelenaLipa.api.rows.DocumentLink;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentLinkRowMapper implements RowMapper<DocumentLink> {

    @Override
    public DocumentLink mapRow(ResultSet rs, int rowNum) throws SQLException {

        DocumentLink documentLink = new DocumentLink();

        documentLink.setGroupId(Integer.parseInt(rs.getString("groupid")));
        documentLink.setDocuId(Integer.parseInt(rs.getString("documentid")));
        documentLink.setTitle(rs.getString("title"));
        documentLink.setType(rs.getString("type"));
        documentLink.setArchiveId(Integer.parseInt(rs.getString("archiveid")));

        String dateOfSubmission = rs.getString("dateofsubmission");
        if(rs.wasNull()) documentLink.setDateOfSubmission("None");
        else documentLink.setDateOfSubmission(dateOfSubmission);

        return documentLink;

    }

}
