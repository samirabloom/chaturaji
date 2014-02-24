package ac.ic.chaturaji.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Size;

/**
 * @author samirarabbanian
 */
public class User extends EqualsHashCodeToString {
    private String id;
    @Size(min = 3, max = 50)
    private String nickname;
    @JsonIgnore
    @Size(min = 6, max = 50)
    private String password;
    @Email
    private String email;

    // Dummy constructor needed to map JSON string back to Java object
    public User() {
    }

    public User(String id, String email, String password, String nickname) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
