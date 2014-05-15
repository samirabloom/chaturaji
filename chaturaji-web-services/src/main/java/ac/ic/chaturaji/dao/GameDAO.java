package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.*;
import ac.ic.chaturaji.uuid.UUIDFactory;
import org.joda.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author samirarabbanian
 */
@Component
public class GameDAO {

    @Resource
    private DataSource dataSource;
    @Resource
    private UUIDFactory uuidFactory;
    @Resource
    private PlayerDAO playerDAO;
    @Resource
    private MoveDAO moveDAO;
    @Resource
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void setupDefaultData() {
        if ("true".equals(System.getProperty("create_default_games"))) {
            List<Game> games = Arrays.asList(
                    new Game(uuidFactory.generateUUID(), new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "as@df.com", passwordEncoder.encode("qazqaz"), "user_one"), Colour.YELLOW, PlayerType.HUMAN)),
                    new Game(uuidFactory.generateUUID(), new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "fd@sa.com", passwordEncoder.encode("qazqaz"), "user_two"), Colour.YELLOW, PlayerType.HUMAN)),
                    new Game(uuidFactory.generateUUID(), new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "qa@qa.com", passwordEncoder.encode("qazqaz"), "user_three"), Colour.YELLOW, PlayerType.HUMAN)),
                    new Game(uuidFactory.generateUUID(), new Player(uuidFactory.generateUUID(), new User(uuidFactory.generateUUID(), "qa@az.com", passwordEncoder.encode("qazqaz"), "user_four"), Colour.YELLOW, PlayerType.HUMAN))
            );
            for (Game game : games) {
                save(game);
            }
        }
    }

    public Collection<Game> getAllWaitingForPlayers() {
        String sql = "SELECT GAME_ID, CREATED_DATE, CURRENT_PLAYER, GAME_STATUS " +
                "FROM GAME NATURAL JOIN PLAYER " +
                "WHERE GAME_STATUS = 0 AND CREATED_DATE >= \'" + new LocalDateTime().minusDays(3).toString("yyyy-MM-dd HH:mm:ss") + "\' " +
                "GROUP BY GAME_ID, CREATED_DATE, CURRENT_PLAYER, GAME_STATUS, CREATED_DATE HAVING count(PLAYER_ID) < 4";
        List<Game> games = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                games.add(getGame(result));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return games;
    }

    public Collection<Game> getFinishedGames(String userId) {
        String sql = "SELECT DISTINCT GAME_ID, CREATED_DATE, CURRENT_PLAYER, GAME_STATUS " +
                "FROM GAME NATURAL JOIN PLAYER NATURAL JOIN USER " +
                "WHERE GAME_STATUS > 1 AND USER_ID = ?";
        List<Game> games = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userId);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                games.add(getGame(result));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return games;
    }

    // SELECT GAME_ID, CREATED_DATE, CURRENT_PLAYER, GAME_STATUS FROM GAME NATURAL JOIN PLAYER WHERE GAME_STATUS = 0 AND CREATED_DATE >= '2014-03-19' GROUP BY GAME_ID HAVING count(PLAYER_ID) < 4;

    public Game get(String id) {
        String sql = "SELECT GAME_ID, CREATED_DATE, CURRENT_PLAYER, GAME_STATUS FROM GAME WHERE GAME_ID=?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return getGame(result);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Game getGame(ResultSet result) throws SQLException {
        Game game = new Game();
        game.setId(result.getString("GAME_ID"));
        game.setCreatedDate(new LocalDateTime(result.getTimestamp("CREATED_DATE").getTime()));
        game.setCurrentPlayerColour(Colour.values()[result.getInt("CURRENT_PLAYER")]);
        game.setGameStatus(GameStatus.values()[result.getInt("GAME_STATUS")]);
        game.setPlayers(playerDAO.getAll(game.getId()));
        game.setMoves(moveDAO.getAll(game.getId()));
        return game;
    }

    public void save(Game game) {
        if (get(game.getId()) == null) {
            String sql = "INSERT INTO GAME(GAME_ID, CREATED_DATE, CURRENT_PLAYER, GAME_STATUS) VALUES (?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, game.getId());
                ps.setTimestamp(2, new java.sql.Timestamp(game.getCreatedDate().toDate().getTime()));
                ps.setInt(3, game.getCurrentPlayerColour().ordinal());
                ps.setInt(4, game.getGameStatus().ordinal());
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException();
                }
                for (Player player : game.getPlayers()) {
                    playerDAO.save(game.getId(), player);
                }
                for (Move move : game.getMoves()) {
                    moveDAO.save(game.getId(), move);
                }
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String sql = "UPDATE GAME SET CREATED_DATE=?, CURRENT_PLAYER=?, GAME_STATUS=? WHERE GAME_ID=?";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setTimestamp(1, new java.sql.Timestamp(game.getCreatedDate().toDate().getTime()));
                ps.setInt(2, game.getCurrentPlayerColour().ordinal());
                ps.setInt(3, game.getGameStatus().ordinal());
                ps.setString(4, game.getId());
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException();
                }
                for (Player player : game.getPlayers()) {
                    playerDAO.save(game.getId(), player);
                }
                for (Move move : game.getMoves()) {
                    moveDAO.save(game.getId(), move);
                }
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
