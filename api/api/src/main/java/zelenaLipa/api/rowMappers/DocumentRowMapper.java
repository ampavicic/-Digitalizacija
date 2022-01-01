package zelenaLipa.api.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import zelenaLipa.api.rows.Document;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentRowMapper implements RowMapper<Document> {

    @Override
    public Document mapRow(ResultSet rs, int rowNum) throws SQLException {

        Document document = new Document();

        document.setGroupId(Integer.parseInt(rs.getString("groupid")));
        document.setDocuId(Integer.parseInt(rs.getString("documentid")));
        document.setContent(rs.getString("content"));
        document.setTitle(rs.getString("title"));
        document.setSubmittedByEmployee(rs.getString("submittedbyemployee"));
        document.setReadByReviser(rs.getString("readbyreviser"));
        document.setArchivedByAccountant(rs.getString("archivedbyaccountant"));
        document.setSignedByDirector(rs.getString("signedbydirector"));
        document.setSentToDirector(rs.getString("senttodirector"));
        document.setType(rs.getString("type"));
        document.setArchiveId(Integer.parseInt(rs.getString("archiveid")));
        document.setUsername(rs.getString("username"));

        String dateOfSubmission = rs.getString("dateofsubmission");
        if(rs.wasNull()) document.setDateOfSubmission("None");
        else document.setDateOfSubmission(dateOfSubmission);

        return document;

    }

}
