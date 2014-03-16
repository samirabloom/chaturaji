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
import java.util.UUID;

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
    @Resource
    private UUIDFactory uuidFactory;

    @Test
    public void shouldAddPlayers() {
        //given
        String gameId = uuidFactory.generateUUID();
        gameDAO.save(new Game(gameId, new Player()));

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            User user = new User(uuidFactory.generateUUID(), "user_" + i + "@email.com", passwordEncoder.encode("password_" + i), "my_nickname" + i);
            userDAO.save(user);
            Player player = new Player(uuidFactory.generateUUID(), user, Colour.BLUE, PlayerType.HUMAN);
            player.setType(PlayerType.AI);
            player.setId(uuidFactory.generateUUID());
            players.add(player);
        }
        for (int i = 0; i < 2; i++) {
            User user = new User(uuidFactory.generateUUID(), "user_" + i + "@email.com", passwordEncoder.encode("password_" + i), "my_nickname" + i);
            userDAO.save(user);
            Player player = new Player(uuidFactory.generateUUID(), user, Colour.BLUE, PlayerType.HUMAN);
            player.setType(PlayerType.HUMAN);
            player.setId(uuidFactory.generateUUID());
            players.add(player);
        }

        //when
        for (Player player : players) {
            playerDAO.save(gameId, player);
        }

        //then
        assertEquals(players, playerDAO.getAll(gameId));

    }
}
