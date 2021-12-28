package zelenaLipa.api.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import zelenaLipa.api.rows.Document;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentRowMapper implements RowMapper<Document> {

    @Override
    public Document mapRow(ResultSet rs, int rowNum) throws SQLException {

        Document document = new Document();

        document.setContent(rs.getString("content"));
        document.setTitle(rs.getString("title"));
        document.setSignature(Integer.parseInt(rs.getString("signature")));
        document.setArchived(Integer.parseInt(rs.getString("archived")));
        document.setType(rs.getString("type"));

        return document;

    }

}
