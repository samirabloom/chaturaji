package ac.ic.chaturaji.model;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        game.setCurrentPlayerColour(Colour.BLUE);
        game.setMoves(moves);
        game.setPlayers(Arrays.asList(playerOne, playerTwo, playerThree));

        // then
        assertEquals("some id", game.getId());
        assertEquals(bitboards, game.getBitboards());
        assertEquals(createdDate, game.getCreatedDate());
        assertEquals(Colour.BLUE, game.getCurrentPlayerColour());
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

    @Test
    public void shouldDetermineNextPlayer() {
        // given
        Game game = new Game("game id", new Player("player one", new User(), Colour.YELLOW, PlayerType.HUMAN));
        game.addPlayer(new Player("player two", new User(), Colour.BLUE, PlayerType.HUMAN));
        game.addPlayer(new Player("player three", new User(), Colour.RED, PlayerType.HUMAN));
        game.addPlayer(new Player("player four", new User(), Colour.GREEN, PlayerType.HUMAN));

        // then
        assertThat(game.getCurrentPlayerColour(), is(Colour.YELLOW));
        assertThat(game.getNextPlayerColour(), is(Colour.BLUE));
        assertThat(game.getNextPlayer(), is(game.getPlayer(1)));

        // when
        game.setCurrentPlayerColour(Colour.BLUE);

        // then
        assertThat(game.getCurrentPlayerColour(), is(Colour.BLUE));
        assertThat(game.getNextPlayerColour(), is(Colour.RED));
        assertThat(game.getNextPlayer(), is(game.getPlayer(2)));

        // when
        game.setCurrentPlayerColour(Colour.RED);

        // then
        assertThat(game.getCurrentPlayerColour(), is(Colour.RED));
        assertThat(game.getNextPlayerColour(), is(Colour.GREEN));
        assertThat(game.getNextPlayer(), is(game.getPlayer(3)));

        // when
        game.setCurrentPlayerColour(Colour.GREEN);

        // then
        assertThat(game.getCurrentPlayerColour(), is(Colour.GREEN));
        assertThat(game.getNextPlayerColour(), is(Colour.YELLOW));
        assertThat(game.getNextPlayer(), is(game.getPlayer(0)));

        // when
        game.setCurrentPlayerColour(Colour.YELLOW);

        // then
        assertThat(game.getCurrentPlayerColour(), is(Colour.YELLOW));
        assertThat(game.getNextPlayerColour(), is(Colour.BLUE));
        assertThat(game.getNextPlayer(), is(game.getPlayer(1)));
    }
}
