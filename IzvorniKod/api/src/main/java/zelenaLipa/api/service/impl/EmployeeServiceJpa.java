package zelenaLipa.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zelenaLipa.api.dao.EmployeeRepository;
import zelenaLipa.api.domain.Employee;
import zelenaLipa.api.service.EmployeeService;

import java.util.List;

@Service
public class EmployeeServiceJpa implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepo;

    @Override
    public boolean isWorkingInCompany(String genId) {
        return employeeRepo.existsById(genId);
    }

    @Override
    public Integer fireEmployee(List<String> pidList) {
        return employeeRepo.deleteByPidIn(pidList);
    }

    @Override
    public List<Employee> filterEmployee(String filter) {
        return employeeRepo.findByNameOrSurnameOrPidOrGenId(filter, filter, filter, filter);
    }

    @Override
    public Employee hireEmployee(Employee employee) {
        return employeeRepo.save(employee);
    }

    @Override
    public Boolean genIdFreeToUse(String genId) {
        return !employeeRepo.existsByGenId(genId);
    }

}
