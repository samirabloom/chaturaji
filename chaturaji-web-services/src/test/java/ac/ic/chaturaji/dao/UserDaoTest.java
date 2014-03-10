package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.model.User;
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
public class UserDaoTest {

    @Resource
    private UserDAO userDAO;

    @Resource
    PasswordEncoder passwordEncoder;

    @Test
    public void shouldSaveAndGetGame() {
        // given
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, "user_one@email.com", passwordEncoder.encode("password_one"), "my_nickname");

        // when
        userDAO.save(user);

        // then
        assertEquals(user, userDAO.get(userId));
    }
}
