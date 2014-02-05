package ac.ic.chaturaji.model;

/**
 * @author samirarabbanian
 */
public class Game extends EqualsHashCodeToString {
    private String id;

    public Game(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
