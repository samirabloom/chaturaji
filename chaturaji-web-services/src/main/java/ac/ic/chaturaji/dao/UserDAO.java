package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @author samirarabbanian
 */
@Component
public class UserDAO {

    @Resource
    DataSource dataSource;
    @Resource
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void setupTestData() {
        save(new User(UUID.randomUUID().toString(), "user_one@email.com", passwordEncoder.encode("password_one"), "my_nickname"));
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM USER WHERE EMAIL=?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, email);
            ResultSet result = ps.executeQuery();
            User user = null;
            while (result.next()) {
                user = new User();
                user.setId(result.getString("USER_ID"));
                user.setEmail(result.getString("EMAIL"));
                user.setNickname(result.getString("NICKNAME"));
                user.setPassword(result.getString("PASSWORD"));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findByNickname(String nickname) {
        String sql = "SELECT * FROM USER WHERE NICKNAME=?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, nickname);
            ResultSet result = ps.executeQuery();
            User user = null;
            while (result.next()) {
                user = new User();
                user.setId(result.getString("USER_ID"));
                user.setEmail(result.getString("EMAIL"));
                user.setNickname(result.getString("NICKNAME"));
                user.setPassword(result.getString("PASSWORD"));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User get(String id) {
        String sql = "SELECT * FROM USER WHERE USER_ID=?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, id);
            ResultSet result = ps.executeQuery();
            User user = null;
            while (result.next()) {
                user = new User();
                user.setId(result.getString("USER_ID"));
                user.setEmail(result.getString("EMAIL"));
                user.setNickname(result.getString("NICKNAME"));
                user.setPassword(result.getString("PASSWORD"));

            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void save(User user) {
        String sql = "INSERT INTO USER (USER_ID,EMAIL,NICKNAME,PASSWORD) VALUES (?,?,?,?)";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, user.getId());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getNickname());
            ps.setString(4, user.getPassword());

            if (ps.executeUpdate() != 1) {
                throw new RuntimeException();
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
