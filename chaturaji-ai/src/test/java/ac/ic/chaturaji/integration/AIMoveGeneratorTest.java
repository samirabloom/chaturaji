package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.ai.AIBoard;
import ac.ic.chaturaji.ai.AIMove;
import ac.ic.chaturaji.ai.AIMoveGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author dg3213
 */
public class AIMoveGeneratorTest {
    AIMoveGenerator validMoves;
    AIBoard theBoard;

    @Before
    public void setUp() {
        validMoves = new AIMoveGenerator();
        theBoard = new AIBoard();
    }

    @Test
    public void testComputeMoves() {
        validMoves.computeMoves(theBoard);

        assertEquals(9, validMoves.GetMoveSize());
    }

    @Test
    public void testGenerateMoves() {
        ArrayList<AIMove> moveList = new ArrayList<>();

        validMoves.generateMoves(theBoard, moveList, theBoard.getCurrentPlayer());

        assertEquals(9, moveList.size());
    }

    @Test
    public void testFindMove() {
        validMoves.computeMoves(theBoard);

        assertNull(validMoves.findMove(0, 0));
    }
}
