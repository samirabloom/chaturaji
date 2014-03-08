package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ac.ic.chaturaji.model.Colour.YELLOW;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RootConfiguration.class, initializers = HSQLApplicationContextInitializer.class)
public class GameDaoTest {

    @Resource
    private GameDAO gameDAO;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    UserDAO userDAO;

    @Test
    public void shouldSaveAndGetGame() {
        // given
        Game game = new Game("test_id", new Player());

        // when
        gameDAO.save(game);

        // then
        assertEquals(game, gameDAO.get("test_id"));
    }

    @Test
    public void shouldSaveMoveAndGetMove(){
        //given
        Game game = new Game("test_id2", new Player());
        Move move = new Move(YELLOW,34,45);

        //when
        gameDAO.save(game);
        gameDAO.saveMove(move,game);

        //then
        assertEquals(move,gameDAO.getMove(game,1));
    }

    @Test
    public void shouldReturnListOfMoves(){
        //given
        Game game = new Game("test_id3", new Player());
        ArrayList<Move> Moves = new ArrayList<>();
        Moves.add(new Move(YELLOW,1,2));
        Moves.add(new Move(Colour.BLUE,3,4));
        Moves.add(new Move(Colour.RED,5,6));
        Moves.add(new Move(Colour.GREEN,7,8));

        //when
        gameDAO.save(game);
        gameDAO.saveMove(Moves.get(0), game);
        gameDAO.saveMove(Moves.get(1), game);
        gameDAO.saveMove(Moves.get(2), game);
        gameDAO.saveMove(Moves.get(3),game);

        //then
        assertEquals(Moves, gameDAO.getAllMoves(game));
    }

    @Test
    public void shouldAddPlayers(){
        //given
        Game game = new Game("test_id4", new Player());
        List<Player> Players = new ArrayList<Player>();
        Player player = null;
        User user = null;
        for(int i = 0; i<4; i++){
            user = new User(UUID.randomUUID().toString(), "user_"+i+"@email.com", passwordEncoder.encode("password_"+i), "my_nickname"+i);
            userDAO.save(user);
            player = new Player( user);
            player.setType(PlayerType.AI);
            player.setColour(Colour.YELLOW);
            player.setId(UUID.randomUUID().toString());
            Players.add(player);
        }

        //when
        gameDAO.save(game);
        gameDAO.savePlayers(game,Players);

        //then
        assertEquals(Players,gameDAO.getPlayers(game));

    }
}
