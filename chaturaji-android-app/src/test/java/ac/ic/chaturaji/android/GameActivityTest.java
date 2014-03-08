package ac.ic.chaturaji.android;


import ac.ic.chaturaji.android.*;
import ac.ic.chaturaji.android.pieces.*;
import org.junit.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by Kadir on March 8.
 */
public class GameActivityTest {

    @Before
    public void setUp() {
        GameActivity game = new GameActivity();
        game.setPieces();
        game.setBoard();
        game.playGame();
        game.setScoreboard();
    }

    @Test
    public void selectPieceTest() {
        GameActivity game = new GameActivity();
        game.setPieces();
        game.setBoard();
        game.playGame();
        game.setScoreboard();

        assertTrue(game.selectPiece(0, 0));
        assertTrue(game.selectPiece(2,2));
    }
}
