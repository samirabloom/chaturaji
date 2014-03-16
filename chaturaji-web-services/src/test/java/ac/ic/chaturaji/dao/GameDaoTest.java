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
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RootConfiguration.class, initializers = HSQLApplicationContextInitializer.class)
public class GameDaoTest {

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
}
