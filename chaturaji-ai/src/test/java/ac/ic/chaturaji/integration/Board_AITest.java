package ac.ic.chaturaji.integration;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import ac.ic.chaturaji.ai.Board_AI;

public class Board_AITest {
    Board_AI theBoard;

    @Before
    public void setUp() {
        theBoard = new Board_AI();
    }
/*
    @Test
    public void testClone() throws Exception {
        Board_AI clone = theBoard.clone();

        assertArrayEquals(theBoard.GetBitBoards(), clone.GetBitBoards());
        assertArrayEquals(theBoard.GetMaterialValue(), clone.GetMaterialValue());
    }
    */
}
