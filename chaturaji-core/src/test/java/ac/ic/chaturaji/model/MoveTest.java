package ac.ic.chaturaji.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class MoveTest {
    @Test
    public void shouldReturnFieldsSetInConstructor() {
        // when
        Move move = new Move(Colour.GREEN, 1, 2);

        // then
        assertEquals(Colour.GREEN, move.getColour());
        assertEquals(1, move.getSource());
        assertEquals(2, move.getDestination());
    }

    @Test
    public void shouldReturnFieldsSetBySetter() {
        // when
        Move move = new Move();
        move.setColour(Colour.GREEN);
        move.setSource(1);
        move.setDestination(2);

        // then
        assertEquals(Colour.GREEN, move.getColour());
        assertEquals(1, move.getSource());
        assertEquals(2, move.getDestination());
    }
}
