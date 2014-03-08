package ac.ic.chaturaji.android;

import android.test.ActivityInstrumentationTestCase2;
import ac.ic.chaturaji.android.pieces.*;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class ac.ic.chaturaji.android.LoginActivityTest \
 * ac.ic.chaturaji.android.tests/android.test.InstrumentationTestRunner
 */
public class GameTest extends ActivityInstrumentationTestCase2<GameActivity> {

    public GameTest() {
        super("ac.ic.chaturaji.android", GameActivity.class);
    }

    private GameActivity game;
    private Pieces[][] Board = new Pieces[8][8];

    @Override
    public void setUp() throws Exception {
        super.setUp();
        game = getActivity();
        game.setBoard(Board);
        game.setPieces();
        Board = game.getBoard();
        //game.setBoard();
        //game.drawPieces();
        //game.playGame();
        //game.setScoreboard();
    }

    public void testselectPiece() {
        //assertNotNull(Board[0][0]);
    }

}
