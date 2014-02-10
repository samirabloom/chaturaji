package ac.ic.chaturaji.model;

/**
 * @author samirarabbanian
 */
public class Player extends EqualsHashCodeToString {
    private String id;
    private PlayerType type;
    private Colour colour;
    private User user;

    public String getId() {
        return id;
    }

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
