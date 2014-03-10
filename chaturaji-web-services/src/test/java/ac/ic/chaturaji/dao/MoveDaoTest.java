package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Move;
import ac.ic.chaturaji.model.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.UUID;

import static ac.ic.chaturaji.model.Colour.YELLOW;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RootConfiguration.class, initializers = HSQLApplicationContextInitializer.class)
public class MoveDaoTest {

    @Resource
    private GameDAO gameDAO;
    @Resource
    private MoveDAO moveDAO;


    // TODO Remove this method as there is no need for this functionality
    /*@Test
    public void shouldSaveMoveAndGetMove() {
        //given
        String gameId = UUID.randomUUID().toString();
        Move move = new Move(UUID.randomUUID().toString(), gameId, YELLOW, 34, 45);

        //when
        moveDAO.saveMove(move, gameId);

        //then
        assertEquals(move, moveDAO.get(gameId, 1));
    }*/

    @Test
    public void shouldReturnListOfMoves() {
        //given
        String gameId = UUID.randomUUID().toString();
        gameDAO.save(new Game(gameId, new Player()));
        ArrayList<Move> moves = new ArrayList<>();
        moves.add(new Move(UUID.randomUUID().toString(), gameId, YELLOW, 1, 2));
        moves.add(new Move(UUID.randomUUID().toString(), gameId, Colour.BLUE, 3, 4));
        moves.add(new Move(UUID.randomUUID().toString(), gameId, Colour.RED, 5, 6));
        moves.add(new Move(UUID.randomUUID().toString(), gameId, Colour.GREEN, 7, 8));

        //when
        moveDAO.saveMove(moves.get(0), gameId);
        moveDAO.saveMove(moves.get(1), gameId);
        moveDAO.saveMove(moves.get(2), gameId);
        moveDAO.saveMove(moves.get(3), gameId);

        //then
        assertEquals(moves, moveDAO.getAll(gameId));
    }
}
