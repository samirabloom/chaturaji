package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.ai.AIBoard;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author dg3213
 */
public class _AIBoardTest {
    AIBoard theBoard;

    @Before
    public void setUp() {
        theBoard = new AIBoard();
    }

    @Ignore
    @Test
    public void testClone() throws Exception {
        AIBoard clone = theBoard.clone();

        assertArrayEquals(theBoard.GetBitBoards(), clone.GetBitBoards());
        assertArrayEquals(theBoard.GetMaterialValue(), clone.GetMaterialValue());
    }
}
