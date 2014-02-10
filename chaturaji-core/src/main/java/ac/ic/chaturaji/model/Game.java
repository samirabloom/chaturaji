package ac.ic.chaturaji.model;

/**
 * @author samirarabbanian
 */
public class Game extends EqualsHashCodeToString {
    private String id;
    private Player[] player = new Player[4];
    private long[] bitboards;
    private Colour currentPlayer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Player[] getPlayer() {
        return player;
    }

    public void setPlayer(Player[] player) {
        this.player = player;
    }

    public long[] getBitboards() {
        return bitboards;
    }

    public void setBitboards(long[] bitboards) {
        this.bitboards = bitboards;
    }

    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Colour currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Game(String id) {
        this.id = id;
    }

}
