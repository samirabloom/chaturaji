package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.User;
import org.joda.time.LocalDateTime;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author samirarabbanian
 */
@Component
public class GameDAO {

    @Resource
    private DataSource dataSource;

    private Map<String, Game> games = new HashMap<>();

    @PostConstruct
    public void setupDefaultData() {
        List<Game> games = Arrays.asList(
                new Game(UUID.randomUUID().toString(), new Player(new User())),
                new Game(UUID.randomUUID().toString(), new Player(new User())),
                new Game(UUID.randomUUID().toString(), new Player(new User())),
                new Game(UUID.randomUUID().toString(), new Player(new User()))
        );
        for (Game game : games) {
            save(game);
        }
    }

    public Collection<Game> getAll() {
        return games.values();
    }

    public Game get(String id) {
//        return games.get(id);
        String sql = "SELECT * FROM GAME WHERE GAME_ID=?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, id);
            ResultSet result = ps.executeQuery();
            Game game = null;
            while (result.next()) {
                game = new Game();
                game.setId(result.getString("GAME_ID"));
                game.setCreatedDate(new LocalDateTime(result.getTimestamp("CREATED_DATE").getTime()));
                game.setCurrentPlayer(Colour.values()[result.getInt("CURRENT_PLAYER")]);
            }
            return game;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Game game) {
//        if (games.containsKey(game.getId())) {
//            games.remove(game.getId());
//        }
//        games.put(game.getId(), game);
        String sql = "INSERT INTO GAME(GAME_ID, CREATED_DATE, CURRENT_PLAYER) VALUES (?,?,?)";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, game.getId());
            ps.setDate(2, new java.sql.Date(game.getCreatedDate().toDate().getTime()));
            ps.setInt(3, game.getCurrentPlayer().ordinal());
            int i = ps.executeUpdate();
            if (i != 1) {
                throw new RuntimeException();
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (games.containsKey(game.getId())) {
            games.remove(game.getId());
        }
        games.put(game.getId(), game);

        //String sql = "INSERT INTO game(ID) VALUES (?,?,?,?,?,?,?)";
    }

}
