package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.User;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author samirarabbanian
 */

//@ImportResource("classpath:/config/dao-context.xml")
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

    }

    public Collection<Game> getAll() {
        return games.values();
    }

    public Game get(String id) {
        return games.get(id);

//
//        try (Connection connection = dataSource.getConnection()) {
//            String sql = "SELECT * FROM game WHERE game_id=?";
//
//            ResultSet result = null;
//            PreparedStatement ps = null;
//
//            ps = connection.prepareStatement(sql);
//
//            ps.setString(1, id);
//            result = ps.executeQuery(sql);
//            Game game = null;
//            while (result.next()) {
//                game = new Game();
//                game.setId(result.getString("game_id"));
//                game.setStartDate(result.getDate("startDate"));
//                game.setCurrentPlayer(Colour.values()[result.getInt("currentPlayer")]);
//            }
//            return game;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void save(Game game) {
        if (games.containsKey(game.getId())) {
            games.remove(game.getId());
        }
        games.put(game.getId(), game);
        String sql = "INSERT game VALUES (?,?,?)";


        try {
            Context envContext = new InitialContext();
            DataSource dataSource = (DataSource)envContext.lookup("java:/comp/env/jdbc/chaturaji-dao");
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = null;

            ps = connection.prepareStatement(sql);

            ps.setString(1, game.getId());

            ps.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
            ps.setInt(3, game.getCurrentPlayer().ordinal());
            int i = ps.executeUpdate();
            if (i != 1)
                throw new RuntimeException();

            ps.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }  catch (NamingException e) {
            e.printStackTrace();
        }

    }

}
