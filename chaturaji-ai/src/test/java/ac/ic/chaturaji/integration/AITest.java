package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.ai.AI;
import ac.ic.chaturaji.ai.GameConstants;
import ac.ic.chaturaji.model.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author dg3213
 */
public class AITest {
    private AI ai;
    private Game game;

    @Before
    public void setup() {
        ai = new AI();
        game = new Game();
    }

    /*------ TESTS ------- */

    @Test
    public void startGameTest() {
        //Game game = new Game();
        game = ai.createGame(game);
        long[] trueBoards;

        assertEquals(Colour.YELLOW, game.getCurrentPlayerColour());
    }

    @Test
    public void submitMoveHumanTest() throws Exception {
        Game game = new Game();
        Move move = new Move();
        game = ai.createGame(game);

        for (int i = 0; i < 4; i++) {
            Player player = new Player();
            player.setType(PlayerType.HUMAN);
            game.addPlayer(player);
        }

        move.setSource(0);
        move.setDestination(18);
        move.setColour(game.getCurrentPlayerColour());

        assertEquals(GameStatus.IN_PLAY, ai.submitMove(game, move).getGameStatus());
    }

    @Test
    public void submitMoveAITest() throws Exception {
        Game game = new Game();
        Move move = new Move();
        game = ai.createGame(game);

        for (int i = 0; i < 4; i++) {
            Player player = new Player();
            player.setType(PlayerType.AI);
            game.addPlayer(player);
        }

        move.setSource(0);
        move.setDestination(18);
        move.setColour(game.getCurrentPlayerColour());

        assertEquals(GameStatus.IN_PLAY, ai.submitMove(game, move).getGameStatus());
    }

    /*
    @Test
    public void gameTest() {
        Game game = new Game();
        Move move = new Move();
        Result result;
        game = ai.createGame(game);
        boolean move_complete = false;

        for (int i = 0; i < 4; i++) {
            Player player = new Player();
            player.setType(PlayerType.AI);
            game.addPlayer(player);
        }

        while (!move_complete) {
        move.setColour(game.getCurrentPlayerColour());
        result = ai.submitMove(game, move);
        if (result.getGameStatus() == GameStatus.GAME_OVER || result.getGameStatus() == GameStatus.STALEMATE)
            move_complete = true;
        }
    }
    */
    @Test
    public void boatTriumphTest() throws Exception {
        int[][] boatTriumph = TestCases.BoatTriumphGame;
        Game game = new Game();
        Move move = new Move();
        Result result = null;
        game = ai.createGame(game);

        for (int i = 0; i < 4; i++) {
            Player player = new Player();
            player.setType(PlayerType.HUMAN);
            game.addPlayer(player);
        }

        for (int[] moves : boatTriumph) {
            int source = moves[0];
            int dest = moves[1];

            move.setColour(game.getCurrentPlayerColour());
            move.setSource(source);
            move.setDestination(dest);

            result = ai.submitMove(game, move);

            assertEquals(GameStatus.IN_PLAY, result.getGameStatus());
        }
        assertFalse(result == null);

        assertEquals(ResultType.BOAT_TRIUMPH, result.getType());
    }

    @Test
    public void promotionTestYellowRed() throws Exception {
        Game game = new Game();
        game = ai.createGame(game);

        long[] PawnBoards = new long[32];

        for (int i = 0; i < 4; i++) {
            Player player = new Player();
            if ((i % 2) == 0)
                player.setType(PlayerType.HUMAN);
            else
                player.setType(PlayerType.AI);
            game.addPlayer(player);
        }

        for (int i = 0; i < 32; i++) {
            PawnBoards[i] = (TestCases.YellowPawns[i] | TestCases.RedPawns[i]);
        }

        game.setBitboards(PawnBoards);
        checkPromo(game);
    }

    @Test
    public void promotionTestBlueGreen() throws Exception {
        Game game = new Game();
        game = ai.createGame(game);

        long[] PawnBoards = new long[32];

        for (int i = 0; i < 4; i++) {
            Player player = new Player();
            if ((i % 2) == 0)
                player.setType(PlayerType.AI);
            else
                player.setType(PlayerType.HUMAN);
            game.addPlayer(player);
        }

        for (int i = 0; i < 32; i++) {
            PawnBoards[i] = (TestCases.BluePawns[i] | TestCases.GreenPawns[i]);
        }

        game.setBitboards(PawnBoards);
        checkPromo(game);
    }

    private void checkPromo(Game game) throws Exception {

        int source, dest;

        int[][] BoatTest = TestCases.BoatPromo;
        int[][] KnightTest = TestCases.KnightPromo;
        int[][] ElephantTest = TestCases.ElephantPromo;
        int[][] KingTest = TestCases.KingPromo;

        Move move = new Move();
        Result result = null;

        for (int k = 0; k < 5; k++) {
            for (int i = 0; i < 4; i++) {
                source = BoatTest[i + (k * 4)][0];
                dest = BoatTest[i + (k * 4)][1];

                move.setColour(game.getCurrentPlayerColour());
                move.setSource(source);
                move.setDestination(dest);

                ai.submitMove(game, move);
            }
            for (int i = 0; i < 4; i++) {
                source = KnightTest[i + (k * 4)][0];
                dest = KnightTest[i + (k * 4)][1];

                move.setColour(game.getCurrentPlayerColour());
                move.setSource(source);
                move.setDestination(dest);

                ai.submitMove(game, move);
            }
            for (int i = 0; i < 4; i++) {
                source = ElephantTest[i + (k * 4)][0];
                dest = ElephantTest[i + (k * 4)][1];

                move.setColour(game.getCurrentPlayerColour());
                move.setSource(source);
                move.setDestination(dest);

                ai.submitMove(game, move);
            }
            for (int i = 0; i < 4; i++) {
                source = KingTest[i + (k * 4)][0];
                dest = KingTest[i + (k * 4)][1];

                move.setColour(game.getCurrentPlayerColour());
                move.setSource(source);
                move.setDestination(dest);

                ai.submitMove(game, move);
            }
        }

        for (int k = 5; k < 6; k++) {
            for (int i = 0; i < 4; i++) {
                source = BoatTest[i + (k * 4)][0];
                dest = BoatTest[i + (k * 4)][1];

                move.setColour(game.getCurrentPlayerColour());
                move.setSource(source);
                move.setDestination(dest);

                ai.submitMove(game, move);

                long boatCount = game.getBitboards()[GameConstants.BOAT + i];

                if (game.getPlayer(i).getType() != PlayerType.AI) {
                    assertTrue(boatCount != 0);
                }
            }
            for (int i = 0; i < 4; i++) {
                source = KnightTest[i + (k * 4)][0];
                dest = KnightTest[i + (k * 4)][1];

                move.setColour(game.getCurrentPlayerColour());
                move.setSource(source);
                move.setDestination(dest);

                ai.submitMove(game, move);

                long knightCount = game.getBitboards()[GameConstants.KNIGHT + i];

                if (game.getPlayer(i).getType() != PlayerType.AI) {
                    assertTrue(knightCount != 0);
                }
            }
            for (int i = 0; i < 4; i++) {
                source = ElephantTest[i + (k * 4)][0];
                dest = ElephantTest[i + (k * 4)][1];

                move.setColour(game.getCurrentPlayerColour());
                move.setSource(source);
                move.setDestination(dest);

                ai.submitMove(game, move);

                long elephantCount = game.getBitboards()[GameConstants.ELEPHANT + i];

                if (game.getPlayer(i).getType() != PlayerType.AI) {
                    assertTrue(elephantCount != 0);
                }
            }
            for (int i = 0; i < 4; i++) {
                source = KingTest[i + (k * 4)][0];
                dest = KingTest[i + (k * 4)][1];

                move.setColour(game.getCurrentPlayerColour());
                move.setSource(source);
                move.setDestination(dest);

                ai.submitMove(game, move);

                long kingCount = game.getBitboards()[GameConstants.KING + i];

                if (game.getPlayer(i).getType() != PlayerType.AI) {
                    assertTrue(kingCount != 0);
                }
            }
        }
    }
}