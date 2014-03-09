package ac.ic.chaturaji.model;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class GameTest {
    @Test
    public void shouldReturnFieldsSetInConstructor() {
        // when
        Player player = new Player();
        Game game = new Game("some id", player);

        // then
        assertEquals("some id", game.getId());
        assertEquals(Arrays.asList(player), game.getPlayers());
    }

    @Test
    public void shouldReturnFieldsSetBySetter() {
        // when
        long[] bitboards = {1, 2, 3};
        LocalDateTime createdDate = new LocalDateTime();
        List<Move> moves = Arrays.asList(new Move());
        Player playerOne = new Player();
        Player playerTwo = new Player();
        Player playerThree = new Player();
        Game game = new Game();
        game.setId("some id");
        game.setBitboards(bitboards);
        game.setCreatedDate(createdDate);
        game.setCurrentPlayer(Colour.BLUE);
        game.setMoves(moves);
        game.setPlayers(Arrays.asList(playerOne, playerTwo, playerThree));

        // then
        assertEquals("some id", game.getId());
        assertEquals(bitboards, game.getBitboards());
        assertEquals(createdDate, game.getCreatedDate());
        assertEquals(Colour.BLUE, game.getCurrentPlayer());
        assertEquals(moves, game.getMoves());
        assertEquals(Arrays.asList(playerOne, playerTwo, playerThree), game.getPlayers());
        assertEquals(playerOne, game.getPlayer(0));
        assertEquals(playerOne, game.getPlayer(1));
        assertEquals(playerOne, game.getPlayer(2));
    }

    @Test
    public void shouldCorrectHandleStalemateCount() {
        // when
        Game game = new Game();

        // then
        assertEquals(0, game.getStalemateCount());

        game.incrementStalemateCount();
        assertEquals(1, game.getStalemateCount());

        game.resetStalemateCount();
        assertEquals(0, game.getStalemateCount());
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotAddMoreThenFourPlayers() {
        // given
        Game game = new Game();

        // when
        game.addPlayer(new Player()); // 1
        game.addPlayer(new Player()); // 2
        game.addPlayer(new Player()); // 3
        game.addPlayer(new Player()); // 4

        // then
        game.addPlayer(new Player()); // illegal
    }
}
