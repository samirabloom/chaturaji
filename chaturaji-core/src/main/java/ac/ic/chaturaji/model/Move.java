package ac.ic.chaturaji.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateTimeSerializer;
import org.joda.time.LocalDateTime;

/**
 * @author samirarabbanian
 */
public class Move extends EqualsHashCodeToString {

    private String id;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdDate;
    private String gameId;
    private Colour colour;
    private int source;
    private int destination;
    private int yellowScore;
    private int blueScore;
    private int redScore;
    private int greenScore;

    public Move() {
        LocalDateTime localDateTime = new LocalDateTime();
        createdDate = new LocalDateTime(localDateTime.getYear(), localDateTime.getMonthOfYear(), localDateTime.getDayOfMonth(), localDateTime.getHourOfDay(), localDateTime.getMinuteOfHour());
    }

    public Move(String id, String gameId, Colour colour, int source, int destination) {
        this();
        this.id = id;
        this.gameId = gameId;
        this.colour = colour;
        this.source = source;
        this.destination = destination;
    }

    public int getYellowScore() {
        return yellowScore;
    }

    public void setYellowScore(int yellowScore) {
        this.yellowScore = yellowScore;
    }

    public int getBlueScore() {
        return blueScore;
    }

    public void setBlueScore(int blueScore) {
        this.blueScore = blueScore;
    }

    public int getRedScore() {
        return redScore;
    }

    public void setRedScore(int redScore) {
        this.redScore = redScore;
    }

    public int getGreenScore() {
        return greenScore;
    }

    public void setGreenScore(int greenScore) {
        this.greenScore = greenScore;
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
