package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.ai.AIMove;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author dg3213
 */
public class AIMoveTest {

    @Test
    public void MoveTest() {
        AIMove theMove = new AIMove();

        theMove.setCaptured(0);
        theMove.setType(0);
        theMove.setPromotion(0);
        theMove.setBoatTriumph(false);

        assertEquals(-1, theMove.getDestination());
        assertEquals(-1, theMove.getPiece());
        assertEquals(-1, theMove.getSource());
        assertEquals(0, theMove.getCaptured());
        assertEquals(0, theMove.getPromoType());
        assertEquals(0, theMove.getType());
        assertEquals(false, theMove.getTriumph());
    }

    @Test
    public void IsEqualTest() {
        AIMove theMove = new AIMove();
        AIMove otherMove = new AIMove();

        theMove.setCaptured(0);
        theMove.setType(0);
        theMove.setPromotion(0);
        theMove.setBoatTriumph(false);

        otherMove.setCaptured(0);
        otherMove.setType(0);
        otherMove.setPromotion(0);
        otherMove.setBoatTriumph(true);

        assertFalse(theMove.IsEqual(otherMove));
    }
}
