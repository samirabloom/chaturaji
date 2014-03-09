package ac.ic.chaturaji.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author samirarabbanian
 */
public class ResultTest {
    @Test
    public void shouldReturnFieldsSetInConstructor() {
        // when
        Move move = new Move();
        Game game = new Game();
        Result result = new Result(GameStatus.GAME_OVER, game, move);

        // then
        assertEquals(GameStatus.GAME_OVER, result.getGameStatus());
        assertSame(game, result.getGame());
        assertSame(move, result.getMove());
    }

    @Test
    public void shouldReturnFieldsSetBySetter() {
        // when
        Move move = new Move();
        Game game = new Game();
        Result result = new Result();
        result.setMove(move);
        result.setGame(game);
        result.setGameStatus(GameStatus.GAME_OVER);
        result.setType(ResultType.BOAT_TRIUMPH);

        // then
        assertEquals(GameStatus.GAME_OVER, result.getGameStatus());
        assertSame(game, result.getGame());
        assertSame(move, result.getMove());
        assertSame(ResultType.BOAT_TRIUMPH, result.getType());
    }
}
