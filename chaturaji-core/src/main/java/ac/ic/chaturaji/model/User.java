package ac.ic.chaturaji.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author samirarabbanian
 */
public class User extends EqualsHashCodeToString {

    public static final String PASSWORD_PATTERN = "^.*(?=.{8,})(?=.*\\d)(?=.*[a-zA-Z]).*$";
    public static final String EMAIL_PATTERN = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

    private String id;
    @Size(min = 3, max = 50, message = "Please provide a name between 3 and 50 characters")
    private String nickname;
    @JsonIgnore
    @Pattern(regexp = PASSWORD_PATTERN, message = "Please provide a password of 8 or more characters with at least 1 digit and 1 letter")
    private String password;
    @Pattern(regexp = EMAIL_PATTERN, message = "Please provide a valid email")
    private String email;
    @JsonIgnore
    private String oneTimeToken;

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

    public String getOneTimeToken() {
        return oneTimeToken;
    }

    public void setOneTimeToken(String oneTimeToken) {
        this.oneTimeToken = oneTimeToken;
    }
}
