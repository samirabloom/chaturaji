package ac.ic.chaturaji.model;

/**
 * @author samirarabbanian
 */
public class Move extends EqualsHashCodeToString {

    private Colour colour;
    private int source;
    private int destination;

    public Move(){}
    public Move(Colour colour, int source, int destination){
        setColour(colour);
        setSource(source);
        setDestination(destination);
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }
}
