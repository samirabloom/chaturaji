package ac.ic.chaturaji.android.pieces.test;

import ac.ic.chaturaji.android.pieces.Boat;
import ac.ic.chaturaji.android.pieces.Knight;
import ac.ic.chaturaji.android.pieces.Pawn;
import ac.ic.chaturaji.android.pieces.Pieces;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Kadir on March 9.
 */
public class PawnTest {

    private Pieces[][] Board;
    private Pawn bluetestpawn;
    private Pawn redtestpawn;
    private Pawn greentestpawn;
    private Pawn yellowtestpawn;
    private boolean[][] valid_moves;

    @Before
    public void setup() {
        bluetestpawn = new Pawn(1, 3); //Blue Pawn
        redtestpawn = new Pawn(2, 4);
        greentestpawn = new Pawn(3, 5);
        yellowtestpawn = new Pawn(4, 3);
        Board = new Pieces[8][8];
        Board[1][1] = bluetestpawn; //Place blue test pawn at B2
        Board[1][5] = redtestpawn; //Place red test pawn at B6
        Board[4][6] = greentestpawn; //Place green test pawn at E7
        Board[6][1] = yellowtestpawn; //Place yellow test pawn at G2
    }

    @Test
    public void test_constructor() {
        assertEquals(1, bluetestpawn.colour);
        assertEquals(3, bluetestpawn.promotion);
        assertEquals(2, redtestpawn.colour);
        assertEquals(4, redtestpawn.promotion);
        assertEquals(3, greentestpawn.colour);
        assertEquals(5, greentestpawn.promotion);
        assertEquals(4, yellowtestpawn.colour);
        assertEquals(3, yellowtestpawn.promotion);
    }

    @Test
    public void test_blue_Valid_moves1() {
        valid_moves = bluetestpawn.valid_moves(1, 1, Board);
        assertTrue(valid_moves[1][2]);
    }

    @Test
    public void test_blue_Valid_moves2() {
        Board[0][2] = new Boat(2);
        Board[2][2] = new Knight(3);
        valid_moves = bluetestpawn.valid_moves(1, 1, Board);
        assertTrue(valid_moves[0][2]);
        assertTrue(valid_moves[1][2]);
        assertTrue(valid_moves[2][2]);
    }

    @Test
    public void test_red_Valid_moves1() {
        valid_moves = redtestpawn.valid_moves(1, 5, Board);
        assertTrue(valid_moves[2][5]);
    }

    @Test
    public void test_red_Valid_moves2() {
        Board[2][4] = new Boat(3);
        Board[2][6] = new Knight(4);
        valid_moves = redtestpawn.valid_moves(1, 5, Board);
        assertTrue(valid_moves[2][4]);
        assertTrue(valid_moves[2][5]);
        assertTrue(valid_moves[2][6]);
    }

    @Test
    public void test_green_Valid_moves1() {
        valid_moves = greentestpawn.valid_moves(4, 6, Board);
        assertTrue(valid_moves[4][5]);
    }

    @Test
    public void test_green_Valid_moves2() {
        Board[3][5] = new Boat(1);
        Board[5][5] = new Knight(2);
        valid_moves = greentestpawn.valid_moves(4, 6, Board);
        assertTrue(valid_moves[3][5]);
        assertTrue(valid_moves[4][5]);
        assertTrue(valid_moves[5][5]);
    }

    @Test
    public void test_yellow_Valid_moves1() {
        valid_moves = yellowtestpawn.valid_moves(6, 1, Board);
        assertTrue(valid_moves[5][1]);
    }

    @Test
    public void test_yellow_Valid_moves2() {
        Board[5][0] = new Boat(1);
        Board[5][2] = new Knight(3);
        valid_moves = yellowtestpawn.valid_moves(6, 1, Board);
        assertTrue(valid_moves[5][0]);
        assertTrue(valid_moves[5][1]);
        assertTrue(valid_moves[5][2]);
    }


}
