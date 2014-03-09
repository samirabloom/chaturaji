package ac.ic.chaturaji.android;

import android.test.ActivityInstrumentationTestCase2;
import ac.ic.chaturaji.android.pieces.*;
import android.widget.ImageView;

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

    public GameTest() throws Exception {
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

    public void testSet_pieces() {

        game.setPieces();
        Board = game.getBoard();

        for(int i = 0; i <= 3; i++)
            assertTrue(Board[i][1] instanceof Pawn);

        for(int i = 4; i <= 7; i++)
            assertTrue(Board[1][i] instanceof Pawn);

        for(int i = 4; i <= 7; i++)
            assertTrue(Board[i][6] instanceof Pawn);

        for(int i = 0; i <= 3; i++)
            assertTrue(Board[6][i] instanceof Pawn);

        assertTrue(Board[0][0] instanceof Boat);
        assertTrue(Board[0][7] instanceof Boat);
        assertTrue(Board[7][7] instanceof Boat);
        assertTrue(Board[7][0] instanceof Boat);

        assertTrue(Board[1][0] instanceof Knight);
        assertTrue(Board[0][6] instanceof Knight);
        assertTrue(Board[6][7] instanceof Knight);
        assertTrue(Board[7][1] instanceof Knight);

        assertTrue(Board[2][0] instanceof Elephant);
        assertTrue(Board[0][5] instanceof Elephant);
        assertTrue(Board[5][7] instanceof Elephant);
        assertTrue(Board[7][2] instanceof Elephant);

        assertTrue(Board[3][0] instanceof King);
        assertTrue(Board[0][4] instanceof King);
        assertTrue(Board[4][7] instanceof King);
        assertTrue(Board[7][3] instanceof King);
    }

}
