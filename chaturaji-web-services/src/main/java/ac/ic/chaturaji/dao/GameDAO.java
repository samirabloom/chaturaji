package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.User;
import org.springframework.stereotype.Component;

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

    public GameDAO() {
        List<Game> games = Arrays.asList(
                new Game(UUID.randomUUID().toString(), new Player(new User())),
                new Game(UUID.randomUUID().toString(), new Player(new User())),
                new Game(UUID.randomUUID().toString(), new Player(new User())),
                new Game(UUID.randomUUID().toString(), new Player(new User()))
        );
        for (Game game : games) {
            save(game);
        }
        try {
            System.out.println(dataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Collection<Game> getAll() {
        return games.values();
    }

    public Game get(String id) {

        String sql = "SELECT * FROM game WHERE id=?";

        ResultSet result = null;

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = null;

            ps = connection.prepareStatement(sql);

            ps.setString(1, id);
            result = ps.executeQuery(sql);
            Game game = null;
            while (result.next()) {
                game = new Game();
                game.setId(result.getString("game_id"));
                game.setStartDate(result.getDate("startDate"));
                game.setCurrentPlayer(Colour.values()[result.getInt("currentPlayer")]);
            }
            return game;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(Game game) {
        String sql = "INSERT game VALUES (?,?,?)";


        try {
            RootConfiguration rootConfiguration = new RootConfiguration();
            dataSource = rootConfiguration.dataSource();
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = null;

            ps = connection.prepareStatement(sql);

            ps.setString(1, game.getId());
            ps.setDate(2, java.sql.Date.valueOf("2014-03-02"));
            ps.setInt(3, game.getCurrentPlayer().ordinal());
            //ps.setString(3,game.getPlayer(0).getId());
            //String player_id = game.getPlayer(1).getId();
            //ps.setString(4,game.getPlayer(1).getId());
            //ps.setString(5,game.getPlayer(2).getId());
            //ps.setString(6,game.getPlayer(3).getId());
            int i = ps.executeUpdate();
            if (i != 1)
                throw new RuntimeException();

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
