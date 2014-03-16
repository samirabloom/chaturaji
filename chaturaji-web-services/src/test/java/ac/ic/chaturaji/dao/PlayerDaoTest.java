package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.model.*;
import ac.ic.chaturaji.uuid.UUIDFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RootConfiguration.class, initializers = HSQLApplicationContextInitializer.class)
public class PlayerDaoTest {

    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    private GameDAO gameDAO;
    @Resource
    private PlayerDAO playerDAO;
    @Resource
    private UserDAO userDAO;
    private UUIDFactory uuidFactory = new UUIDFactory();

    @Test
    public void shouldSavePlayers() {
        //given
        Player createGamePlayer = new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "as@df.com", "qazqaz", "user_one"), Colour.YELLOW, PlayerType.HUMAN);
        Game game = new Game(uuidFactory.generateUUID(), createGamePlayer);
        gameDAO.save(game);

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            User user = new User(uuidFactory.generateUUID(), "user_" + i + "@email.com", passwordEncoder.encode("password_" + i), "my_nickname" + i);
            userDAO.save(user);
            Player player = new Player(uuidFactory.generateUUID(), user, Colour.BLUE, PlayerType.HUMAN);
            player.setGameId(game.getId());
            players.add(player);
        }

        //when
        for (Player player : players) {
            playerDAO.save(game.getId(), player);
        }

        //then
        players.add(0, createGamePlayer);
        assertEquals(players, playerDAO.getAll(game.getId()));
    }

    @Test
    public void shouldUpdatePlayers() {
        //given
        Game game = new Game(uuidFactory.generateUUID(), new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "as@df.com", "qazqaz", "user_two"), Colour.YELLOW, PlayerType.HUMAN))
        ;
        gameDAO.save(game);
        User user = new User(uuidFactory.generateUUID(), "user_update@email.com", passwordEncoder.encode("password_update"), "my_nickname");
        userDAO.save(user);
        Player player = new Player(uuidFactory.generateUUID(), user, Colour.BLUE, PlayerType.HUMAN);
        player.setGameId(game.getId());
        player.setPoints(0);

        // when
        playerDAO.save(game.getId(), player);
        assertEquals(player, playerDAO.get(player.getId()));

        //when
        player.setType(PlayerType.AI);
        player.setPoints(200);
        playerDAO.save(game.getId(), player);

        //then
        assertEquals(player, playerDAO.get(player.getId()));
    }
}
