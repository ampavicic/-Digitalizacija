package zelenaLipa.api.rows;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//@Entity
//@Table(name = "UserAccount")
public class UserAccount {

    @Size(min = 2, max = 30)
    private String username;

    @NotNull
    @Size(min = 8, max = 50)
    private String password;

    @NotNull
    @Size(min = 5, max = 50)
    @Email
    private String email;

    @NotNull
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
