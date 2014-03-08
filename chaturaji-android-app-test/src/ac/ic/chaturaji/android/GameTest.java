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

    private GameActivity game;
    private Pieces[][] Board;
    private ImageView[][] BoardImage;
    private int selected_column;
    private int selected_row;
    private boolean[][] valid_moves;
    private boolean moved;
    private String movelist;
    private int blue_score;
    private int red_score;
    private int green_score;
    private int yellow_score;
    private int move_count;
    private int blue_king_captured_by;
    private int red_king_captured_by;
    private int green_king_captured_by;
    private int yellow_king_captured_by;

    public GameTest() {
        super(GameActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        game = getActivity();
        Board = game.getBoard();
        BoardImage = game.getBoardImage();
        selected_column = game.getSelected_column();
        selected_row = game.getSelected_row();
        valid_moves = game.getValid_moves();
        moved = game.getMoved();
        movelist = game.getMovelist();
        blue_score = game.getBlue_score();
        red_score = game.getRed_score();
        green_score = game.getGreen_score();
        yellow_score = game.getYellow_score();
        move_count = game.getMove_count();
        blue_king_captured_by = game.getBlue_king_captured_by();
        red_king_captured_by = game.getRed_king_captured_by();
        green_king_captured_by = game.getGreen_king_captured_by();
        yellow_king_captured_by = game.getYellow_king_captured_by();
    }

    public void testPreconditions() {

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                assertTrue(BoardImage[i][j] != null);
                assertFalse(valid_moves[i][j]);
            }

        assertEquals(-1, selected_column);
        assertEquals(-1, selected_row);
        assertFalse(moved);
        assertEquals("", movelist);
        assertEquals(0, blue_score);
        assertEquals(0, red_score);
        assertEquals(0, green_score);
        assertEquals(0, yellow_score);
        assertEquals(0, move_count);
        assertEquals(0, blue_king_captured_by);
        assertEquals(0, red_king_captured_by);
        assertEquals(0, green_king_captured_by);
        assertEquals(0, yellow_king_captured_by);
    }

    public void testBoard() {

        assertNotNull(Board[0][0]);
        assertNotNull(Board[0][1]);
        assertNull(Board[0][2]);
    }

}
