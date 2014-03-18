package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.model.*;
import ac.ic.chaturaji.uuid.UUIDFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.ArrayList;

import static ac.ic.chaturaji.model.Colour.YELLOW;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RootConfiguration.class, initializers = HSQLApplicationContextInitializer.class)
public class MoveDAOTest {

    @Resource
    private GameDAO gameDAO;
    @Resource
    private MoveDAO moveDAO;
    private UUIDFactory uuidFactory = new UUIDFactory();

    @Test
    public void shouldReturnListOfMoves() {
        //given
        String gameId = uuidFactory.generateUUID();
        gameDAO.save(new Game(gameId, new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "as@df.com", "qazqaz", "user_one"), Colour.YELLOW, PlayerType.HUMAN)));
        ArrayList<Move> moves = new ArrayList<>();
        Move moveOne = new Move(uuidFactory.generateUUID(), gameId, YELLOW, 1, 2);
        moveOne.setId(uuidFactory.generateUUID());
        moves.add(moveOne);
        Move moveTwo = new Move(uuidFactory.generateUUID(), gameId, Colour.BLUE, 3, 4);
        moveTwo.setId(uuidFactory.generateUUID());
        moves.add(moveTwo);
        Move moveThree = new Move(uuidFactory.generateUUID(), gameId, Colour.RED, 5, 6);
        moveThree.setId(uuidFactory.generateUUID());
        moves.add(moveThree);
        Move moveFour = new Move(uuidFactory.generateUUID(), gameId, Colour.GREEN, 7, 8);
        moveFour.setId(uuidFactory.generateUUID());
        moves.add(moveFour);

        //when
        moveDAO.save(gameId, moves.get(0));
        moveDAO.save(gameId, moves.get(1));
        moveDAO.save(gameId, moves.get(2));
        moveDAO.save(gameId, moves.get(3));

        //then
        assertEquals(moves, moveDAO.getAll(gameId));
    }
}
