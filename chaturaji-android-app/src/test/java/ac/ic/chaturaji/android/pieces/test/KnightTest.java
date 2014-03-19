package ac.ic.chaturaji.android.pieces.test;

import ac.ic.chaturaji.android.pieces.King;
import ac.ic.chaturaji.android.pieces.Knight;
import ac.ic.chaturaji.android.pieces.Pieces;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Kadir
 */
public class KnightTest {

    private Pieces[][] Board;
    private Knight testknight;
    private boolean[][] valid_moves;

    @Before
    public void setup() {
        testknight = new Knight(1);
        Board = new Pieces[8][8];
        Board[3][3] = testknight;
    }

    @Test
    public void test_constructor() {
        assertEquals(1, testknight.colour);
    }

    @Test
    public void test_Valid_moves1() {
        valid_moves = testknight.valid_moves(3, 3, Board);
        assertTrue(valid_moves[2][1]);
        assertTrue(valid_moves[2][5]);
        assertTrue(valid_moves[4][1]);
        assertTrue(valid_moves[4][5]);
        assertTrue(valid_moves[1][2]);
        assertTrue(valid_moves[1][4]);
        assertTrue(valid_moves[5][2]);
        assertTrue(valid_moves[5][4]);
    }

    @Test
    public void test_Valid_moves2() {
        Board[2][1] = new King(3);
        Board[2][5] = new King(1);
        valid_moves = testknight.valid_moves(3, 3, Board);
        assertTrue(valid_moves[2][1]);
        assertFalse(valid_moves[2][5]);
        assertTrue(valid_moves[4][1]);
        assertTrue(valid_moves[4][5]);
        assertTrue(valid_moves[1][2]);
        assertTrue(valid_moves[1][4]);
        assertTrue(valid_moves[5][2]);
        assertTrue(valid_moves[5][4]);
    }

}
