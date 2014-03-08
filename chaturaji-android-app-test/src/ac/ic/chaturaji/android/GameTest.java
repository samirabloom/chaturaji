package ac.ic.chaturaji.android;

import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import ac.ic.chaturaji.android.pieces.*;
import android.widget.Button;
import android.widget.ImageView;

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
        super(GameActivity.class);
    }

    private GameActivity game;
    private Pieces[][] Board;
    private ImageView A1;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        game = getActivity();
        Board = game.getBoard();
        A1 = (ImageView) game.findViewById(R.id.A1);
    }

    public void testPreconditions() {

        assertTrue(A1 != null);
    }

    public void testBoard() {

        assertNotNull(Board[0][0]);
    }

}
