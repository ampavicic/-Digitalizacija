package zelenaLipa.api.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import zelenaLipa.api.domain.Document;

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
        document.setSubmittedByEmployee(rs.getBoolean("submittedbyemployee"));
        document.setReadByReviser(rs.getBoolean("readbyreviser"));
        document.setArchivedByAccountant(rs.getBoolean("archivedbyaccountant"));
        document.setSignedByDirector(rs.getBoolean("signedbydirector"));
        document.setSentToDirector(rs.getBoolean("senttodirector"));
        document.setType(rs.getString("type"));
        document.setArchiveId(Integer.parseInt(rs.getString("archiveid")));
        document.setUsername(rs.getString("username"));

        String dateOfSubmission = rs.getString("dateofsubmission");
        if(rs.wasNull()) document.setDateOfSubmission(null);
        else document.setDateOfSubmission(null);

        return document;

    }

}
