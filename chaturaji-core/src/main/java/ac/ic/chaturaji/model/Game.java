package ac.ic.chaturaji.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateTimeSerializer;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author samirarabbanian
 */
public class Game extends EqualsHashCodeToString {
    private String id;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdDate;
    private List<Player> players = new ArrayList<>();
    private Colour currentPlayerColour = Colour.YELLOW;
    private GameStatus gameStatus = GameStatus.NOT_STARTED;
    /* JsonIgnore the following fields to prevent them from being sent down to the client,
    this is important otherwise the performance will be ridiculously slow and unresponsive */
    @JsonIgnore
    private List<Move> moves = new ArrayList<>();
    @JsonIgnore
    private long[] bitboards;
    @JsonIgnore
    private int stalemateCount;

    // Dummy constructor needed to map JSON string back to Java object
    public Game() {
        LocalDateTime localDateTime = new LocalDateTime();
        createdDate = new LocalDateTime(localDateTime.getYear(), localDateTime.getMonthOfYear(), localDateTime.getDayOfMonth(), localDateTime.getHourOfDay(), localDateTime.getMinuteOfHour());
    }

    public Game(String id, Player player) {
        this();
        this.id = id;
        player.setGameId(id);
        players.add(player);
    }

    protected String[] fieldsExcludedFromEqualsAndHashCode() {
        // TODO - REMOVE THIS EXCLUSION ONCE PLAYERS ARE SAVED IN THE DB CORRECTLY
        return new String[]{"players"};
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        for (Player player : players) {
            player.setGameId(id);
        }
        this.players = players;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
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

    public Colour getCurrentPlayerColour() {
        return currentPlayerColour;
    }

    public void setCurrentPlayerColour(Colour currentPlayer) {
        this.currentPlayerColour = currentPlayer;
    }

    @JsonIgnore
    public Colour getNextPlayerColour() {
        Colour[] colours = Colour.values();
        return colours[(currentPlayerColour.ordinal() + 1) % colours.length];
    }

    @JsonIgnore
    public Player getNextPlayer() {
        return players.get(getNextPlayerColour().ordinal());
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getStalemateCount() {
        return stalemateCount;
    }

    public void incrementStalemateCount() {
        stalemateCount++;
    }

    public void resetStalemateCount() {
        stalemateCount = 0;
    }

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public Game addPlayer(Player player) {
        if (players.size() < 4) {
            player.setGameId(id);
            players.add(player);
        } else {
            throw new RuntimeException("Game already has four players");
        }
        return this;
    }
}