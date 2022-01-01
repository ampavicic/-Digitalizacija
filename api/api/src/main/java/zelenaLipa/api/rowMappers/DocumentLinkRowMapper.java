package zelenaLipa.api.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import zelenaLipa.api.rows.Document;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentLinkRowMapper implements RowMapper<Document> {

    @Override
    public Document mapRow(ResultSet rs, int rowNum) throws SQLException {

        Document documentLink = new Document();

        documentLink.setGroupId(Integer.parseInt(rs.getString("groupid")));
        documentLink.setDocuId(Integer.parseInt(rs.getString("documentid")));
        documentLink.setTitle(rs.getString("title"));
        documentLink.setSubmittedByEmployee(rs.getString("submittedbyemployee"));
        documentLink.setReadByReviser(rs.getString("readbyreviser"));
        documentLink.setArchivedByAccountant(rs.getString("archivedbyaccountant"));
        documentLink.setSignedByDirector(rs.getString("signedbydirector"));
        documentLink.setSentToDirector(rs.getString("senttodirector"));
        documentLink.setType(rs.getString("type"));
        documentLink.setArchiveId(Integer.parseInt(rs.getString("archiveid")));
        documentLink.setUsername(rs.getString("username"));
        documentLink.setContent(null);

        String dateOfSubmission = rs.getString("dateofsubmission");
        if(rs.wasNull()) documentLink.setDateOfSubmission("None");
        else documentLink.setDateOfSubmission(dateOfSubmission);

        return documentLink;

    }

}
