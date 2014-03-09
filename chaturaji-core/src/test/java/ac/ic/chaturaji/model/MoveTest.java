package ac.ic.chaturaji.model;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class MoveTest {
    @Test
    public void shouldReturnFieldsSetInConstructor() {
        // when
        Move move = new Move("move id", "game id", Colour.GREEN, 1, 2);

        // then
        assertEquals("move id", move.getId());
        assertEquals("game id", move.getGameId());
        assertEquals(Colour.GREEN, move.getColour());
        assertEquals(1, move.getSource());
        assertEquals(2, move.getDestination());
    }

    @Test
    public void shouldReturnFieldsSetBySetter() {
        // when
        Move move = new Move();
        move.setId("move id");
        move.setGameId("game id");
        move.setColour(Colour.GREEN);
        move.setSource(1);
        move.setDestination(2);

        // then
        assertEquals("move id", move.getId());
        assertEquals("game id", move.getGameId());
        assertEquals(Colour.GREEN, move.getColour());
        assertEquals(1, move.getSource());
        assertEquals(2, move.getDestination());
    }
}
