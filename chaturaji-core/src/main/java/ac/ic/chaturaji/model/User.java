package ac.ic.chaturaji.model;

/**
 * @author samirarabbanian
 */
public class User extends EqualsHashCodeToString {
    private String id;
    private String nickname;

    // Dummy constructor needed to map JSON string back to Java object
    public User() {
    }

    public User(String id, String nickname) {
        this.id = id;
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
}
