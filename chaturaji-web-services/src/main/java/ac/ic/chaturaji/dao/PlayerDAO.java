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
        String sql = "INSERT INTO PLAYER(PLAYER_ID,GAME_ID, USER_ID,COLOUR,TYPE) VALUES (?,?,?,?,?)";
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
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException();
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Player> getAll(String gameId) {
        String sql = "SELECT * FROM PLAYER WHERE GAME_ID=?";
        List<Player> players = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, gameId);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Player player = new Player();
                String id = result.getString("PLAYER_ID");
                player.setId(id);
                player.setUser(userDAO.get(result.getString("USER_ID")));
                player.setColour(Colour.values()[result.getInt("COLOUR")]);
                if (result.getString("TYPE").equals("AI")) {
                    player.setType(PlayerType.AI);
                } else {
                    player.setType(PlayerType.HUMAN);
                }
                players.add(player);
            }
            return players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
