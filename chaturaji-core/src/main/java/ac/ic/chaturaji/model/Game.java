package ac.ic.chaturaji.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author samirarabbanian
 */
public class Game extends EqualsHashCodeToString {
    private String id;
    private Date startDate;
    private List<Player> players = new ArrayList<>();
    @JsonIgnore
    private long[] bitboards;
    private Colour currentPlayer;

    // Dummy constructor needed to map JSON string back to Java object
    public Game() {
    }

    public Game(String id, Player player) {
        this.id = id;
        players.add(player);
    }

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

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
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

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public void addPlayer(Player player) {
        if (players.size() < 4) {
            players.add(player);
        } else {
            throw new RuntimeException("Game already has four players");
        }
    }
}
