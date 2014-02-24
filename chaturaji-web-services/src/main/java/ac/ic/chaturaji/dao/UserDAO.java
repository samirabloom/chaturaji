package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author samirarabbanian
 */
@Component
public class UserDAO {

    private Map<String, User> usersById = new HashMap<>();
    private Map<String, User> usersByEmail = new HashMap<>();
    @Resource
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void setupTestData() {
        save(new User(UUID.randomUUID().toString(), "user_one@email.com", passwordEncoder.encode("password_one"), "my_nickname"));
    }

    public User findByEmail(String email) {
        return usersByEmail.get(email);
    }

    public User get(String id) {
        return usersById.get(id);
    }

    public void save(User user) {
        usersByEmail.put(user.getEmail(), user);
        usersById.put(user.getId(), user);
    }
}
