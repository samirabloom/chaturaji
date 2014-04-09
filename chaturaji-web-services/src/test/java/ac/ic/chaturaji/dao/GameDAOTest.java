package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.model.*;
import ac.ic.chaturaji.uuid.UUIDFactory;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RootConfiguration.class, initializers = HSQLApplicationContextInitializer.class)
public class GameDAOTest {

    @Resource
    private GameDAO gameDAO;
    @Resource
    private UUIDFactory uuidFactory;

    @Resource
    PasswordEncoder passwordEncoder;

    @Test
    public void shouldSaveAndGetGame() {
        // given
        String gameId = uuidFactory.generateUUID();
        Game game = new Game(gameId, new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "as@df.com", "qazqaz", "user_one"), Colour.YELLOW, PlayerType.HUMAN));

        // when
        gameDAO.save(game);

        // then
        assertEquals(game, gameDAO.get(gameId));
    }

    @Test
    public void shouldRetrieveAllGamesWaitingForPlayer() {
        // given
        Collection<Game> existingGames = gameDAO.getAllWaitingForPlayers();

        Game gameOver = new Game(uuidFactory.generateUUID(), new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "one@email.com", "qazqaz", "user_one"), Colour.YELLOW, PlayerType.HUMAN));
        gameOver.setGameStatus(GameStatus.GAME_OVER);
        gameDAO.save(gameOver);

        Game oldGame = new Game(uuidFactory.generateUUID(), new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "one@email.com", "qazqaz", "user_one"), Colour.YELLOW, PlayerType.HUMAN));
        oldGame.setCreatedDate(new LocalDateTime().minusDays(10));
        gameDAO.save(oldGame);

        Game gameFourPlayers = new Game(uuidFactory.generateUUID(), new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "one@email.com", "qazqaz", "user_one"), Colour.YELLOW, PlayerType.HUMAN));
        gameFourPlayers.addPlayer(new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "two@email.com", "qazqaz", "user_two"), Colour.BLUE, PlayerType.HUMAN));
        gameFourPlayers.addPlayer(new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "three@email.com", "qazqaz", "user_three"), Colour.BLUE, PlayerType.HUMAN));
        gameFourPlayers.addPlayer(new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "four@email.com", "qazqaz", "user_four"), Colour.BLUE, PlayerType.HUMAN));
        gameDAO.save(gameFourPlayers);

        // when
        Collection<Game> allWaitingForPlayers = gameDAO.getAllWaitingForPlayers();

        // then
        assertEquals(existingGames, allWaitingForPlayers);
    }
}
