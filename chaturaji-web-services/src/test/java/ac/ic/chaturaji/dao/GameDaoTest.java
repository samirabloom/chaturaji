package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RootConfiguration.class, initializers = HSQLApplicationContextInitializer.class)
public class GameDaoTest {

    @Resource
    private GameDAO gameDAO;

    @Test
    public void shouldSaveAndGetGame() {
        // given
        Game game = new Game("test_id", new Player());

        // when
        gameDAO.save(game);

        // then
        assertEquals(game, gameDAO.get("test_id"));
    }
}
