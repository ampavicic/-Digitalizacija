package zelenaLipa.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @Size(min = 9, max = 9)
    @Column(name = "genid")
    private String genId;

    @Column(unique = true, name = "pid")
    @NotNull
    @Size(min = 11, max = 11)
    private String pid; //OIB

    @NotNull
    @Size(max = 20)
    @Column(name = "name")
    private String name;

    @NotNull
    @Size(max = 20)
    @Column(name = "surname")
    private String surname;

    @NotNull
    @Size(max = 70)
    @Column(name = "residence")
    private String residence;

    @NotNull
    @Column(name = "salary")
    private Integer salary;

    @NotNull
    @Column(name = "roleid")
    private Integer roleId;

    public String getGenId() {
        return genId;
    }

    public void setGenId(String genId) {
        this.genId = genId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "genId='" + genId + '\'' +
                ", pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", residence='" + residence + '\'' +
                ", salary=" + salary +
                ", roleId=" + roleId +
                '}';
    }
}
