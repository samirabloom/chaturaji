package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.ai.Board_AI;
import ac.ic.chaturaji.ai.MoveGenerator_AI;
import ac.ic.chaturaji.ai.Move_AI;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author dg3213
 */
public class MoveGenerator_AITest {
    MoveGenerator_AI validMoves;
    Board_AI theBoard;

    @Before
    public void setUp() {
        validMoves = new MoveGenerator_AI();
        theBoard = new Board_AI();
    }

    @Test
    public void testComputeMoves() {
        validMoves.ComputeMoves(theBoard);

        assertEquals(9, validMoves.GetMoveSize());
    }

    @Test
    public void testGenerateMoves() {
        ArrayList<Move_AI> moveList = new ArrayList<>();

        validMoves.GenerateMoves(theBoard, moveList, theBoard.GetCurrentPlayer());

        assertEquals(9, moveList.size());
    }

    @Test
    public void testFindMove() {
        validMoves.ComputeMoves(theBoard);

        assertNull(validMoves.FindMove(0, 0));
    }
}
