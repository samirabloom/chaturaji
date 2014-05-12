package ac.ic.chaturaji.model;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
        assertEquals(8, game.getAILevel());
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
        game.setAILevel(5);
        game.setBitboards(bitboards);
        game.setCreatedDate(createdDate);
        game.setCurrentPlayerColour(Colour.BLUE);
        game.setMoves(moves);
        game.setPlayers(Arrays.asList(playerOne, playerTwo, playerThree));

        // then
        assertEquals("some id", game.getId());
        assertEquals(5, game.getAILevel());
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
        game.addPlayer(new Player("player two", new User(), Colour.BLUE, PlayerType.AI));
        game.addPlayer(new Player("player three", new User(), Colour.RED, PlayerType.HUMAN));
        game.addPlayer(new Player("player four", new User(), Colour.GREEN, PlayerType.AI));

        // then
        assertThat(game.getCurrentPlayerColour(), is(Colour.YELLOW));
        assertThat(game.getCurrentPlayerType(), is(PlayerType.HUMAN));
        assertThat(game.getNextPlayerColour(), is(Colour.BLUE));

        // when
        game.setCurrentPlayerColour(Colour.BLUE);

        // then
        assertThat(game.getCurrentPlayerColour(), is(Colour.BLUE));
        assertThat(game.getCurrentPlayerType(), is(PlayerType.AI));
        assertThat(game.getNextPlayerColour(), is(Colour.RED));

        // when
        game.setCurrentPlayerColour(Colour.RED);

        // then
        assertThat(game.getCurrentPlayerColour(), is(Colour.RED));
        assertThat(game.getCurrentPlayerType(), is(PlayerType.HUMAN));
        assertThat(game.getNextPlayerColour(), is(Colour.GREEN));

        // when
        game.setCurrentPlayerColour(Colour.GREEN);

        // then
        assertThat(game.getCurrentPlayerColour(), is(Colour.GREEN));
        assertThat(game.getCurrentPlayerType(), is(PlayerType.AI));
        assertThat(game.getNextPlayerColour(), is(Colour.YELLOW));

        // when
        game.setCurrentPlayerColour(Colour.YELLOW);

        // then
        assertThat(game.getCurrentPlayerColour(), is(Colour.YELLOW));
        assertThat(game.getCurrentPlayerType(), is(PlayerType.HUMAN));
        assertThat(game.getNextPlayerColour(), is(Colour.BLUE));
    }


    @Test
    public void shouldCalculateWinningPlayers() {
        Game game = new Game();
        Player playerOne = new Player();
        playerOne.setPoints(0);
        game.addPlayer(playerOne);
        Player playerTwo = new Player();
        playerTwo.setPoints(10);
        game.addPlayer(playerTwo);
        Player playerThree = new Player();
        playerThree.setPoints(5);
        game.addPlayer(playerThree);
        Player playerFour = new Player();
        playerFour.setPoints(3);
        game.addPlayer(playerFour);

        assertThat(game.getPlayersWithHighestScore(), contains(playerTwo));
        assertThat(game.getPlayersWithHighestScore(), hasSize(1));
    }

    @Test
    public void shouldCalculateWinningPlayersWhenDraw() {
        Game game = new Game();
        Player playerOne = new Player();
        playerOne.setPoints(0);
        game.addPlayer(playerOne);
        Player playerTwo = new Player();
        playerTwo.setPoints(10);
        game.addPlayer(playerTwo);
        Player playerThree = new Player();
        playerThree.setPoints(5);
        game.addPlayer(playerThree);
        Player playerFour = new Player();
        playerFour.setPoints(10);
        game.addPlayer(playerFour);

        assertThat(game.getPlayersWithHighestScore(), containsInAnyOrder(playerTwo, playerFour));
        assertThat(game.getPlayersWithHighestScore(), hasSize(2));
    }
}
