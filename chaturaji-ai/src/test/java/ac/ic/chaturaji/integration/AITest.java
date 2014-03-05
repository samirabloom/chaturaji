package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.ai.AI;
import ac.ic.chaturaji.model.Game;
import org.junit.Before;

import java.io.File;

/**
 * Created by dg3213 on 04/03/14.
 */
public class AITest{
    private AI ai;
    private Game game;
    private String filepath;

    @Before
    public void setup() {
        ai = new AI();
        game = new Game();
        filepath = new File("").getAbsolutePath();
    }

    /*------ HELPER FUNCTIONS ------*/

  /*  private long ConvertToBinary(String word) {
        int length = word.length();
        long bitboard = 0;
        long count = 1;

        assertEquals(64, length);

        int[] results = new int[length];

        for (int i = 0; i < length; i++)
            results[i] = word.charAt(i) - '0';

        for (int i = 0; i < 64; i++, count <<= 1) {
            if (results[i] == 1)
                bitboard |= count;
        }
        return bitboard;
    }

    private void CheckBitBoardsEqual(long[] theBoard, long[] trueBoard, int start, int end) {

        for (int i = start; i <= end; i++) {
            assertEquals(trueBoard[i - start], theBoard[i]);
        }
    }

    private long[] GetBoards(String filename) {
        BufferedReader reader = null;
        long[] trueBoards = new long[32];

        for (int i = 0; i < 32; i++) {
            trueBoards[i] = 0;
        }

        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            int count = 0;

            while (line != null && count < 32) {
                trueBoards[count] = ConvertToBinary(line);
                count++;
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trueBoards;
    }

    private Move_AI GetMove(String filename) {
        BufferedReader reader = null;
        int[] move = new int[6];
        Move_AI theMove = null;
        boolean triumph = false;

        for (int i = 0; i < 6; i++) {
            move[i] = 0;
        }

        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            int count = 0;

            while (line != null && count < 6) {
                move[count] = Integer.parseInt(line);
                count++;
                line = reader.readLine();
            }
            triumph = Boolean.parseBoolean(line);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int dest = move[0];
        int source = move[1];
        int piece = move[2];
        int captured = move[3];
        int type = move[4];
        int promo = move[5];

        theMove = new Move_AI(piece, source, dest);
        theMove.SetCaptured(captured);
        theMove.SetType(type);
        theMove.SetPromotion(promo);
        theMove.SetBoatTriumph(triumph);

        return theMove;

    }

    /*------ TESTS -------

    @Test
    public void startGameTest() {
        //Game game = new Game();
        game = ai.startGame(game);
        long[] trueBoards;

        assertEquals(Colour.YELLOW, game.getCurrentPlayer());

        Board_AI board = new Board_AI(game.getBitboards(), game.getCurrentPlayer().ordinal());
        Board_AI cloneBoard = board.clone();

        String testPath = filepath.concat("/chaturaji-ai/src/test/java/ac/ic/chaturaji/integration/TestCases/DefaultBoard.txt");

        trueBoards = GetBoards(testPath);

        CheckBitBoardsEqual(board.GetBitBoards(), trueBoards, 20, 23);
        CheckBitBoardsEqual(board.GetBitBoards(), cloneBoard.GetBitBoards(), 0, 31);
    }

    @Test
    public void submitMoveTest() {
        Game game = new Game();
        Move move = new Move();
        game = ai.startGame(game);

        String testPath = filepath.concat("/chaturaji-ai/src/test/java/ac/ic/chaturaji/integration/TestCases/Move2.txt");

        for (int i = 0; i < 4; i++) {
            Player player = new Player();
            player.setType(PlayerType.HUMAN);
            game.addPlayer(player);
        }

        Move_AI theMove = GetMove(testPath);
        move.setSource(theMove.getSource());
        move.setDestination(theMove.getDest());
        move.setColour(game.getCurrentPlayer());

        Result result = ai.submitMove(game, move);

        assertEquals(GameStatus.IN_PLAY, result.getGameStatus());
    }*/
}
