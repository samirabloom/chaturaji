package ac.ic.chaturaji.android.pieces.test;

import ac.ic.chaturaji.android.pieces.Boat;
import ac.ic.chaturaji.android.pieces.Elephant;
import ac.ic.chaturaji.android.pieces.Pieces;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Kadir
 */
public class ElephantTest {

    private Pieces[][] Board;
    private Elephant testelephant;
    private boolean[][] valid_moves;

    @Before
    public void setup() {
        testelephant = new Elephant(1);
        Board = new Pieces[8][8];
        Board[3][3] = testelephant;
    }

    @Test
    public void test_constructor() {
        assertEquals(1, testelephant.colour);
    }

    @Test
    public void test_Valid_moves1() {
        valid_moves = testelephant.valid_moves(3, 3, Board);
        assertTrue(valid_moves[3][0]);
        assertTrue(valid_moves[3][1]);
        assertTrue(valid_moves[3][2]);
        assertTrue(valid_moves[3][4]);
        assertTrue(valid_moves[3][5]);
        assertTrue(valid_moves[3][6]);
        assertTrue(valid_moves[3][7]);
        assertTrue(valid_moves[0][3]);
        assertTrue(valid_moves[1][3]);
        assertTrue(valid_moves[2][3]);
        assertTrue(valid_moves[4][3]);
        assertTrue(valid_moves[5][3]);
        assertTrue(valid_moves[6][3]);
        assertTrue(valid_moves[7][3]);
    }

    @Test
    public void test_Valid_moves2() {
        Board[3][5] = new Boat(1);
        Board[3][1] = new Boat(1);
        Board[1][3] = new Boat(2);
        Board[5][3] = new Boat(3);
        valid_moves = testelephant.valid_moves(3, 3, Board);
        assertTrue(valid_moves[3][4]);
        assertFalse(valid_moves[3][5]);
        assertTrue(valid_moves[3][2]);
        assertFalse(valid_moves[3][1]);
        assertTrue(valid_moves[2][3]);
        assertTrue(valid_moves[1][3]);
        assertTrue(valid_moves[4][3]);
        assertTrue(valid_moves[5][3]);
    }

    @Test
    public void test_Valid_moves3() {
        Board[3][5] = new Boat(2);
        Board[3][1] = new Boat(3);
        Board[1][3] = new Boat(1);
        Board[5][3] = new Boat(1);
        valid_moves = testelephant.valid_moves(3, 3, Board);
        assertTrue(valid_moves[3][4]);
        assertTrue(valid_moves[3][5]);
        assertTrue(valid_moves[3][2]);
        assertTrue(valid_moves[3][1]);
        assertTrue(valid_moves[2][3]);
        assertFalse(valid_moves[1][3]);
        assertTrue(valid_moves[4][3]);
        assertFalse(valid_moves[5][3]);
    }
}
