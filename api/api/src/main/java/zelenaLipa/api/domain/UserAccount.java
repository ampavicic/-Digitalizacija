package zelenaLipa.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity()
@Table(name="useraccount")
public class UserAccount {

    @Id
    @Size(min = 2, max = 50)
    @Column(name = "username")
    private String username;

    @NotNull
    @Size(min = 4, max = 255)
    @Column(name = "password")
    private String password;

    @NotNull
    @Size(min = 5, max = 50)
    @Email
    @Column(name = "email")
    private String email;

    @NotNull
    @Column(unique = true, name = "genid")
    @Size(min = 9, max = 9)
    private String genId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenId() {
        return genId;
    }

    public void setGenId(String genId) {
        this.genId = genId;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", genId='" + genId + '\'' +
                '}';
    }
}
