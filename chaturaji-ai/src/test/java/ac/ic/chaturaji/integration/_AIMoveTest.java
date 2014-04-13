package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.ai.AIMove;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author dg3213
 */
public class _AIMoveTest {

    @Test
    public void MoveTest() {
        AIMove theMove = new AIMove();

        theMove.SetCaptured(0);
        theMove.SetType(0);
        theMove.SetPromotion(0);
        theMove.SetBoatTriumph(false);

        assertEquals(-1, theMove.getDest());
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

        theMove.SetCaptured(0);
        theMove.SetType(0);
        theMove.SetPromotion(0);
        theMove.SetBoatTriumph(false);

        otherMove.SetCaptured(0);
        otherMove.SetType(0);
        otherMove.SetPromotion(0);
        otherMove.SetBoatTriumph(true);

        assertFalse(theMove.IsEqual(otherMove));
    }
}
