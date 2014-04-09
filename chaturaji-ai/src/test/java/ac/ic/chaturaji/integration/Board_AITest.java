package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.ai.Board_AI;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author dg3213
 */
public class Board_AITest {
    Board_AI theBoard;

    @Before
    public void setUp() {
        theBoard = new Board_AI();
    }

    @Ignore
    @Test
    public void testClone() throws Exception {
        Board_AI clone = theBoard.clone();

        assertArrayEquals(theBoard.GetBitBoards(), clone.GetBitBoards());
        assertArrayEquals(theBoard.GetMaterialValue(), clone.GetMaterialValue());
    }
}
