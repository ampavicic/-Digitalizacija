package zelenaLipa.api.service;

import zelenaLipa.api.domain.Employee;

import java.util.List;

public interface EmployeeService {

    boolean isWorkingInCompany(String genId);

    Integer fireEmployee(List<String> pidList);

    List<Employee> filterEmployee(String filter);

    Employee hireEmployee(Employee employee);

    Boolean genIdFreeToUse(String genId);

}
