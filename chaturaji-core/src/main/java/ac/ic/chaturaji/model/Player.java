package ac.ic.chaturaji.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author samirarabbanian
 */
public class Player extends EqualsHashCodeToString {
    private String id;
    private String gameId;
    private PlayerType type;
    private Colour colour;
    private User user;
    private int points;
    private Set<Integer> kingsCaptured = new HashSet<>();
    private boolean noMorePieces;

    // Dummy constructor needed to map JSON string back to Java object
    public Player() {
    }

    public Player(String id, User user, Colour colour, PlayerType type) {
        this.id = id;
        this.user = user;
        this.colour = colour;
        this.type = type;
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

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Set<Integer> getKingsCaptured() {
        return kingsCaptured;
    }

    public void setKingsCaptured(Set<Integer> kingsCaptured) {
        this.kingsCaptured = kingsCaptured;
    }

    public boolean isNoMorePieces() {
        return noMorePieces;
    }

    public void setNoMoves(boolean noMorePieces) {
        this.noMorePieces = noMorePieces;
    }
}
