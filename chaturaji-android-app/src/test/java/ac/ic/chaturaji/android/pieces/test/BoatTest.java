package ac.ic.chaturaji.android.pieces.test;

import ac.ic.chaturaji.android.pieces.Boat;
import ac.ic.chaturaji.android.pieces.Pieces;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Kadir on March 9.
 */
public class BoatTest {

    private Pieces[][] Board;
    private Boat testboat;
    private boolean[][] valid_moves;

    @Before
    public void setup() {
        testboat = new Boat(1);
        Board = new Pieces[8][8];
        Board[3][3] = testboat;
    }

    @Test
    public void test_constructor() {
        assertEquals(1, testboat.colour);
    }

    @Test
    public void test_Valid_moves() {
        valid_moves = testboat.valid_moves(3, 3, Board);
        assertTrue(valid_moves[1][1]);
        assertTrue(valid_moves[5][5]);
        assertTrue(valid_moves[5][1]);
        assertTrue(valid_moves[1][5]);
    }
}
