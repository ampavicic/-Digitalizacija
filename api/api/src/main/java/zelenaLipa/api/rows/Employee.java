package zelenaLipa.api.rows;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//@Entity
//@Table(name = "Employee")
public class Employee {

    @Size(min = 9, max = 9)
    private String genId;

    @NotNull
    @Size(min = 11, max = 11)
    private String pid; //OIB

    @NotNull
    @Size(max = 20)
    private String name;

    @NotNull
    @Size(max = 20)
    private String surname;

    @NotNull
    @Size(max = 70)
    private String residence;

    @NotNull
    private long salary;

    private long roleId;

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

    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
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
