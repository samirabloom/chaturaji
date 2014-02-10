package ac.ic.chaturaji.model;

/**
 * @author samirarabbanian
 */
public class User extends EqualsHashCodeToString {
    private String id;
    private String nickname;

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
