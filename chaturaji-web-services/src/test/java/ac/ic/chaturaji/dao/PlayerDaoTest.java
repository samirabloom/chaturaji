package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.PlayerType;
import ac.ic.chaturaji.model.User;
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
    private PlayerDAO playerDAO;
    @Resource
    private UserDAO userDAO;

    @Test
    public void shouldAddPlayers() {
        //given
        String gameId = "gameId";
        List<Player> Players = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            User user = new User(UUID.randomUUID().toString(), "user_" + i + "@email.com", passwordEncoder.encode("password_" + i), "my_nickname" + i);
            userDAO.save(user);
            Player player = new Player(UUID.randomUUID().toString(), user);
            player.setType(PlayerType.AI);
            player.setColour(Colour.YELLOW);
            player.setId(UUID.randomUUID().toString());
            Players.add(player);
        }

        //when
        playerDAO.save(gameId, Players);

        //then
        assertEquals(Players, playerDAO.getAll(gameId));

    }
}
