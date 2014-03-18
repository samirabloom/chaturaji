package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.model.User;
import ac.ic.chaturaji.uuid.UUIDFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = RootConfiguration.class, initializers = HSQLApplicationContextInitializer.class)
public class UserDAOTest {

    @Resource
    private UserDAO userDAO;
    @Resource
    private PasswordEncoder passwordEncoder;
    private UUIDFactory uuidFactory = new UUIDFactory();

    @Test
    public void shouldSaveAndGetUser() {
        // given
        String userId = uuidFactory.generateUUID();
        User user = new User(userId, "user_one@email.com", passwordEncoder.encode("password_one"), "my_nickname");

        // when
        userDAO.save(user);

        // then
        assertEquals(user, userDAO.get(userId));
    }

    @Test
    public void shouldSaveAndGetUserByEmail() {
        // given
        String userId = uuidFactory.generateUUID();
        User user = new User(userId, "user_three@email.com", passwordEncoder.encode("password_three"), "my_nickname3");

        // when
        userDAO.save(user);

        // then
        assertEquals(user, userDAO.findByEmail("user_three@email.com"));
    }

    @Test
    public void shouldSaveAndGetUserByNickname() {
        // given
        String userId = uuidFactory.generateUUID();
        User user = new User(userId, "user_four@email.com", passwordEncoder.encode("password_four"), "my_nickname4");

        // when
        userDAO.save(user);

        // then
        assertEquals(user, userDAO.findByNickname("my_nickname4"));
    }
}
