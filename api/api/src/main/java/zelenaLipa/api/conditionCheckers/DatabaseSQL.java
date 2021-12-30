package zelenaLipa.api.conditionCheckers;

import org.springframework.jdbc.core.JdbcTemplate;
import zelenaLipa.api.rowMappers.*;
import zelenaLipa.api.rows.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

/*Sve funkcije koje pristupaju BP*/
public class DatabaseSQL {

    private static String[] columns = {"submittedbyemployee", "readbyreviser", "signedbydirector", "archivedbyaccountant", "senttodirector"};

    public static List<DocumentLink> getDocumentLinks(Roles role, boolean roleBoolean, JdbcTemplate jdbcTemplate) {
        String sqlGetDocumentLinks = "SELECT groupid, documentid, title, type, dateofsubmission, archiveid FROM document WHERE " + columns[role.getValue()] + " = true ORDER BY dateofsubmission DESC;";
        List<DocumentLink> documentLinks = jdbcTemplate.query(sqlGetDocumentLinks, new DocumentLinkRowMapper());
        return documentLinks;
    }

    public static List<Document> getDocument(int docuId, String username, JdbcTemplate jdbcTemplate) {
        String sqlGetDocument;
        if(username == null) sqlGetDocument = "SELECT content, title, type, archivedbyaccountant, signedbydirector, readbyreviser, submittedbyemployee, dateofsubmission, senttodirector, archiveid FROM document WHERE documentid = " + docuId + " ORDER BY dateofsubmission DESC;";
        else sqlGetDocument = "SELECT content, title, type, archivedbyaccountant, signedbydirector, readbyreviser, submittedbyemployee, dateofsubmission, senttodirector, archiveid FROM document WHERE username = '" + username + "' AND documentid = " + docuId + " ORDER BY dateofsubmission DESC;";
        List<Document> documents = jdbcTemplate.query(sqlGetDocument, new DocumentRowMapper());
        return documents;
    }

    public static int updateColumn(int docuId, Roles role, boolean timestampBoolean, JdbcTemplate jdbcTemplate) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String sqlUpdateColumn;
        if(!timestampBoolean) sqlUpdateColumn = "UPDATE document SET " + columns[role.getValue()] + " = true WHERE documentid = " + docuId + ";";
        else sqlUpdateColumn = "UPDATE document SET " + columns[role.getValue()] + " = true, dateofsubmission = '" + timestamp + "'::TIMESTAMP(0) WHERE documentid = " + docuId + ";";
        int result = jdbcTemplate.update(sqlUpdateColumn);
        return result;
    }

    public static List<DocumentLink> getDocumentLinksByUsernameAndGroupId(int groupId, String username, JdbcTemplate jdbcTemplate) {
        String sqlGetDocumentLinksByUsernameAndGroupId = "SELECT groupid, documentid, title, type, dateofsubmission, archiveid FROM document WHERE username = '" + username + "' AND groupid = " + groupId + " ORDER BY dateofsubmission DESC;";
        List<DocumentLink> documentLinks = jdbcTemplate.query(sqlGetDocumentLinksByUsernameAndGroupId, new DocumentLinkRowMapper());
        return documentLinks;
    }

    public static List<DocumentLink> getDocumentLinksByUsername(String username, JdbcTemplate jdbcTemplate) {
        String sqlGetDocumentLinksByUsername = "SELECT groupid, documentid, title, type, dateofsubmission, archiveid FROM document WHERE username = '" + username + "' ORDER BY dateofsubmission DESC;";
        List<DocumentLink> documentLinks = jdbcTemplate.query(sqlGetDocumentLinksByUsername, new DocumentLinkRowMapper());
        return documentLinks;
    }

    public static int addDocumentByUsername(int docuId, String username, String resultOCR, int groupId, String title, String designation, JdbcTemplate jdbcTemplate) {
        String sqlAddDocumentByUsername = "INSERT INTO document (documentid, submittedbyemployee, archivedbyaccountant, signedbydirector, readbyreviser, senttodirector, username, content, groupid, title, type, dateofsubmission, archiveid) VALUES("
                + docuId + ", "
                + "false" + ", "
                + "false" + ", "
                + "false" + ", "
                + "false" + ", "
                + "false" + ", '"
                + username + "', '"
                + resultOCR + "', "
                + groupId + ", '"
                + title + "', '"
                + designation + "', "
                + "null" + ", "
                + "-1);";
        int result = jdbcTemplate.update(sqlAddDocumentByUsername);
        return result;
    }

    public static boolean existsByDocumentId(int docuId, JdbcTemplate jdbcTemplate) {
        String sqlExists = "SELECT EXISTS(SELECT documentid FROM document WHERE documentid = " + docuId + ") AS exists;";
        boolean exists;
        List<String> result = jdbcTemplate.query(sqlExists, (rs, rowNum) -> { return rs.getString("exists"); });
        if(result.get(0).equals("t")) exists = true;
        else exists = false;
        return exists;
    }

    public static boolean existsByGroupId(int groupId, JdbcTemplate jdbcTemplate) {
        String sqlExists = "SELECT EXISTS(SELECT documentid FROM document WHERE groupid = " + groupId + ") AS exists;";
        boolean exists;
        List<String> result = jdbcTemplate.query(sqlExists, (rs, rowNum) -> { return rs.getString("exists"); });
        if(result.get(0).equals("t")) exists = true;
        else exists = false;
        return exists;
    }

    /*SPECIFIČNO ZA DIREKTORA*/
    public static int removeEmployee(String[] checkBoxes, JdbcTemplate jdbcTemplate) {
        String sqlRemoveEmployee = "DELETE FROM employee WHERE ";
        for(int i = 0; i < checkBoxes.length - 1; i++) { sqlRemoveEmployee += "pid = '" + checkBoxes[i] + "' OR "; }
        sqlRemoveEmployee += "pid = '" + checkBoxes[checkBoxes.length - 1] + "';";
        int result = jdbcTemplate.update(sqlRemoveEmployee);
        return result;
    }

    /*SPECIFIČNO ZA DIREKTORA*/
    public static ResultPair addEmployee(String pid, String name, String surname, String residence, String salary, String roleId, JdbcTemplate jdbcTemplate) {
        String genIdString;
        boolean exists = false;
        Random rand = new Random(System.currentTimeMillis());
        do {
            genIdString = String.valueOf(rand.nextInt(900000000) + 100000000);
            String sqlExists = "SELECT EXISTS(SELECT genid FROM employee WHERE genid = '" + genIdString + "') AS exists;";
            List<String> result = jdbcTemplate.query(sqlExists, (rs, rowNum) -> {
                return rs.getString("exists");
            });
            if (result.get(0).equals("t")) exists = true;
            else exists = false;
        } while (exists);
        String sqlAddEmployee = "INSERT INTO employee (pid, genid, name, surname, residence, salary, roleid) VALUES ( '"
                + pid + "', '"
                + genIdString + "', '"
                + name + "', '"
                + surname + "', '"
                + residence + "', "
                + salary + ", "
                + roleId + ");";

        int result = jdbcTemplate.update(sqlAddEmployee);
        return new DatabaseSQL.ResultPair(result, genIdString);
    }

    /*SPECIFIČNO ZA DIREKTORA*/
    public static List<Role> getRoles(JdbcTemplate jdbcTemplate) {
        String sqlGetRoles = "SELECT * FROM role;";
        List<Role> roles = jdbcTemplate.query(sqlGetRoles, new RoleRowMapper());
        return roles;
    }

    /*SPECIFIČNO ZA DIREKTORA*/
    public static List<Employee> filterEmployees(String filter, JdbcTemplate jdbcTemplate) {
        String sqlFilterEmployees = "SELECT * FROM employee WHERE name = '" + filter + "' OR surname = '" + filter + "' OR genid = '" + filter + "' OR pid = '" + filter + "';";
        List<Employee> employees = jdbcTemplate.query(sqlFilterEmployees, new EmployeeRowMapper());
        return employees;
    }

    /*SPECIFIČNO ZA NEULOGIRANOG KORISNIKA*/
    public static List<Employee> getEmployee(String genId, JdbcTemplate jdbcTemplate) {
        String sqlGetEmployee = "SELECT * FROM employee WHERE genid = '" + genId + "';";             //Provjeri jeli korisnik zaposlen u tvrtci
        List<Employee> employees = jdbcTemplate.query(sqlGetEmployee, new EmployeeRowMapper());
        return employees;
    }

    /*SPECIFIČNO ZA NEULOGIRANOG KORISNIKA*/
    public static List<UserAccount> getUserAccount(String genId, JdbcTemplate jdbcTemplate) {
        String sqlGetUserAccount = "SELECT * FROM useraccount WHERE genid = '" + genId + "';";    //Ispitaj jeli zaposlenik vec ima otvoren racun
        List<UserAccount> userAccounts = jdbcTemplate.query(sqlGetUserAccount, new UserAccountRowMapper());
        return userAccounts;
    }

    /*SPECIFIČNO ZA NEULOGIRANOG KORISNIKA*/
    public static int addNewUserAccount(String username, String encodedPassword, String email, String genId, JdbcTemplate jdbcTemplate) {
        String sqlAddNewUserAccount = "INSERT INTO useraccount (username, password, email, genid, enabled) VALUES " +
                "( '"
                + username + "', '"
                + encodedPassword + "', '"
                + email + "', '"
                + genId + "', " +
                " true);";
        int result = jdbcTemplate.update(sqlAddNewUserAccount);
        return result;
    }

    /*SPECIFIČNO ZA RAČUNOVOĐU*/
    public static boolean existsByArchiveId(int archiveId, JdbcTemplate jdbcTemplate) {
        String sqlExists = "SELECT EXISTS(SELECT documentid FROM document WHERE documentid = " + archiveId + ") AS exists;";
        boolean exists;
        List<String> result = jdbcTemplate.query(sqlExists, (rs, rowNum) -> { return rs.getString("exists"); });
        if(result.get(0).equals("t")) exists = true;
        else exists = false;
        return exists;
    }

    /*SPECIFIČNO ZA RACČUNOVOĐU*/
    public static int updateArchiveId(int docuId, int archiveId, JdbcTemplate jdbcTemplate) {
        String sqlArchiveId = "UPDATE document SET archiveid = " + archiveId + " WHERE documentid = " + docuId + ";";
        int result = jdbcTemplate.update(sqlArchiveId);
        return result;
    }


    public enum Roles {
        ROLE_REVISER_CHECK_IF_SUBMITTED (0), ROLE_ACCOUNTANT_CHECK_IF_READ (1), ROLE_ACCOUNTANT_CHECK_IF_SIGNED(2), ROLE_ACCOUNTANT_GET_ARCHIVED (3), ROLE_DIRECTOR_CHECK_IF_SENT(4),
        ROLE_EMPLOYEE_UPDATE_SUBMIT(0), ROLE_REVISER_UPDATE_READ(1), ROLE_DIRECTOR_UPDATE_SIGNED(2), ROLE_ACCOUNTANT_UPDATE_ARCHIVED(3), ROLE_ACCOUNTANT_UPDATE_SENT(4);
        private int column;

        Roles(int column)
        {
            this.column = column;
        }

        public int getValue() {
            return this.column;
        }

    }

    public static class ResultPair {
        public int result;
        public String variable;
        public ResultPair(int result, String variable) { this.result = result; this.variable = variable; }
    }

}
