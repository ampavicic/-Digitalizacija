package zelenaLipa.api.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import zelenaLipa.api.domain.Employee;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeRowMapper implements RowMapper<Employee> {

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {

        Employee employee = new Employee();

        employee.setPid(rs.getString("pid"));
        employee.setGenId(rs.getString("genid"));
        employee.setName(rs.getString("name"));
        employee.setSurname(rs.getString("surname"));
        employee.setResidence(rs.getString("residence"));
        employee.setSalary(rs.getInt("salary"));
        employee.setRoleId(rs.getInt("roleId"));

        return employee;

    }

}
