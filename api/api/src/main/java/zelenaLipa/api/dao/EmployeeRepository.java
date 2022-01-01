package zelenaLipa.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import zelenaLipa.api.domain.Employee;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Transactional
    Integer deleteByPidIn(List<String> pidList);

    Boolean existsByGenId(String genId);

    List<Employee> findByNameOrSurnameOrPidOrGenId(String name, String surname, String pid, String genId);

}
