package ac.ic.chaturaji.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Date;

/**
 * @author samirarabbanian
 */
public class Game extends EqualsHashCodeToString {
    private String id;
    private Date startDate;
    private Player[] player = new Player[4];
    @JsonIgnore
    private long[] bitboards;
    private Colour currentPlayer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Player[] getPlayer() {
        return player;
    }

    public void setPlayer(Player[] player) {
        this.player = player;
    }

    /**
     * Needed by AI but not for client to use
     */
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

    // Dummy constructor needed to map JSON string back to Java object
    public Game() {
    }

    public Game(String id) {
        this.id = id;
    }


}
