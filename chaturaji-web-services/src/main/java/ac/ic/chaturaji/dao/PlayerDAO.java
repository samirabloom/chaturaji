package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.PlayerType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author samirarabbanian
 */
@Component
public class PlayerDAO {

    @Resource
    private UserDAO userDAO;
    @Resource
    private DataSource dataSource;

    public void save(String gameId, Player player) {
        if (get(player.getId()) == null) {
            String sql = "INSERT INTO PLAYER(PLAYER_ID, GAME_ID, USER_ID, COLOUR, TYPE, POINTS) VALUES (?,?,?,?,?,?)";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, player.getId());
                ps.setString(2, gameId);
                ps.setString(3, player.getUser().getId());
                ps.setInt(4, player.getColour().ordinal());
                if (player.getType() == PlayerType.AI) {
                    ps.setString(5, "AI");
                } else {
                    ps.setString(5, "HUMAN");
                }
                ps.setInt(6, player.getPoints());
                userDAO.save(player.getUser());
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException();
                }
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String sql = "UPDATE PLAYER SET GAME_ID=?, USER_ID=?, COLOUR=?, TYPE=?, POINTS=? WHERE PLAYER_ID=?";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, gameId);
                ps.setString(2, player.getUser().getId());
                ps.setInt(3, player.getColour().ordinal());
                if (player.getType() == PlayerType.AI) {
                    ps.setString(4, "AI");
                } else {
                    ps.setString(4, "HUMAN");
                }
                ps.setInt(5, player.getPoints());
                ps.setString(6, player.getId());
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException();
                }
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Player> getAll(String gameId) {
        String sql = "SELECT PLAYER_ID, GAME_ID, USER_ID, COLOUR, TYPE, POINTS FROM PLAYER WHERE GAME_ID=?";
        List<Player> players = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, gameId);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                players.add(buildPlayer(result));
            }
            return players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Player get(String id) {
        String sql = "SELECT PLAYER_ID, GAME_ID, USER_ID, COLOUR, TYPE, POINTS FROM PLAYER WHERE PLAYER_ID=?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, id);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                return buildPlayer(result);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Player buildPlayer(ResultSet result) throws SQLException {
        Player player = new Player();
        player.setId(result.getString("PLAYER_ID"));
        player.setGameId(result.getString("GAME_ID"));
        player.setUser(userDAO.get(result.getString("USER_ID")));
        player.setColour(Colour.values()[result.getInt("COLOUR")]);
        if (result.getString("TYPE").equals("AI")) {
            player.setType(PlayerType.AI);
        } else {
            player.setType(PlayerType.HUMAN);
        }
        player.setPoints(result.getInt("POINTS"));
        return player;
    }
}
