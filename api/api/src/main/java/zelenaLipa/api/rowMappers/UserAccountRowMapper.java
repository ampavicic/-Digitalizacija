package zelenaLipa.api.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import zelenaLipa.api.rows.UserAccount;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAccountRowMapper implements RowMapper<UserAccount> {

    @Override
    public UserAccount mapRow(ResultSet rs, int rowNum) throws SQLException {

        UserAccount userAccount = new UserAccount();

        userAccount.setUsername(rs.getString("username"));
        userAccount.setPassword(rs.getString("password"));
        userAccount.setEmail(rs.getString("email"));
        userAccount.setGenId(rs.getString("genid"));

        return userAccount;

    }

}
