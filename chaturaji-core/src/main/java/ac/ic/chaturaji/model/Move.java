package ac.ic.chaturaji.model;

/**
 * @author samirarabbanian
 */
public class Move extends EqualsHashCodeToString {

    private String id;
    private String gameId;
    private Colour colour;
    private int source;
    private int destination;

    public Move() {
    }

    public Move(String id, String gameId, Colour colour, int source, int destination) {
        this.id = id;
        this.gameId = gameId;
        this.colour = colour;
        this.source = source;
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }
}
