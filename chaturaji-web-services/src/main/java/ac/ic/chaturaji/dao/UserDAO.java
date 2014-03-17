package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.User;
import ac.ic.chaturaji.uuid.UUIDFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author samirarabbanian
 */
@Component
public class UserDAO {

    @Resource
    private DataSource dataSource;
    @Resource
    private UUIDFactory uuidFactory;
    @Resource
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void setupTestData() {
        save(new User(uuidFactory.generateUUID(), "user_one@email.com", passwordEncoder.encode("password_one"), "my_nickname"));
    }

    public User findByEmail(String email) {
        String sql = "SELECT USER_ID, EMAIL, NICKNAME, PASSWORD, ONE_TIME_TOKEN FROM USER WHERE EMAIL=?";

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
                user.setOneTimeToken(result.getString("ONE_TIME_TOKEN"));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findByNickname(String nickname) {
        String sql = "SELECT USER_ID, EMAIL, NICKNAME, PASSWORD, ONE_TIME_TOKEN FROM USER WHERE NICKNAME=?";

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
                user.setOneTimeToken(result.getString("ONE_TIME_TOKEN"));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User get(String id) {
        String sql = "SELECT USER_ID, EMAIL, NICKNAME, PASSWORD, ONE_TIME_TOKEN FROM USER WHERE USER_ID=?";

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
                user.setOneTimeToken(result.getString("ONE_TIME_TOKEN"));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void save(User user) {
        if (get(user.getId()) == null) {
            String sql = "INSERT INTO USER (USER_ID, EMAIL, NICKNAME, PASSWORD, ONE_TIME_TOKEN) VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, user.getId());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getNickname());
                ps.setString(4, user.getPassword());
                ps.setString(5, user.getOneTimeToken());

                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException();
                }
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String sql = "UPDATE USER SET EMAIL=?, NICKNAME=?, PASSWORD=?, ONE_TIME_TOKEN=? WHERE USER_ID=?";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, user.getEmail());
                ps.setString(2, user.getNickname());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getOneTimeToken());
                ps.setString(5, user.getId());

                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException();
                }
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
