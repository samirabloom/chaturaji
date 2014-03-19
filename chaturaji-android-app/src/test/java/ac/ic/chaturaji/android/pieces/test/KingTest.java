package ac.ic.chaturaji.android.pieces.test;

import ac.ic.chaturaji.android.pieces.King;
import ac.ic.chaturaji.android.pieces.Pieces;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Kadir
 */
public class KingTest {

    private Pieces[][] Board;
    private King testking;
    private boolean[][] valid_moves;

    @Before
    public void setup() {
        testking = new King(1);
        Board = new Pieces[8][8];
        Board[3][3] = testking;
    }

    @Test
    public void test_constructor() {
        assertEquals(1, testking.colour);
    }

    @Test
    public void test_Valid_moves() {
        valid_moves = testking.valid_moves(3, 3, Board);
        assertTrue(valid_moves[3][4]);
        assertTrue(valid_moves[3][2]);
        assertTrue(valid_moves[2][3]);
        assertTrue(valid_moves[4][3]);
        assertTrue(valid_moves[4][4]);
        assertTrue(valid_moves[2][2]);
        assertTrue(valid_moves[4][2]);
        assertTrue(valid_moves[2][4]);
    }
}
