package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Move;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author samirarabbanian
 */
@Component
public class MoveDAO {

    @Resource
    private DataSource dataSource;

    public void save(String gameId, Move move) {
        if (get(move.getId()) == null) {
            String sql = "INSERT INTO MOVE(MOVE_ID, GAME_ID, COLOUR, SOURCE, DESTINATION) VALUES (?,?,?,?,?)";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, move.getId());
                ps.setString(2, gameId);
                ps.setInt(3, move.getColour().ordinal());
                ps.setInt(4, move.getSource());
                ps.setInt(5, move.getDestination());
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException();
                }
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String sql = "UPDATE MOVE SET GAME_ID=?, COLOUR=?, SOURCE=?, DESTINATION=? WHERE MOVE_ID=?";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, gameId);
                ps.setInt(2, move.getColour().ordinal());
                ps.setInt(3, move.getSource());
                ps.setInt(4, move.getDestination());
                ps.setString(5, move.getId());
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException();
                }
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ArrayList<Move> getAll(String gameId) {
        String sql = "SELECT MOVE_ID, GAME_ID, COLOUR, SOURCE, DESTINATION FROM MOVE WHERE GAME_ID=?";
        ArrayList<Move> moves = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, gameId);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Move move = new Move();

                move.setId(result.getString("MOVE_ID"));
                move.setGameId(result.getString("GAME_ID"));
                move.setColour(Colour.values()[result.getInt("COLOUR")]);
                move.setSource(result.getInt("SOURCE"));
                move.setDestination(result.getInt("DESTINATION"));
                moves.add(move);
            }
            return moves;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Move get(String moveId) {
        String sql = "SELECT MOVE_ID, GAME_ID, COLOUR, SOURCE, DESTINATION FROM MOVE WHERE MOVE_ID=?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, moveId);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                Move move = new Move();

                move.setId(result.getString("MOVE_ID"));
                move.setGameId(result.getString("GAME_ID"));
                move.setColour(Colour.values()[result.getInt("COLOUR")]);
                move.setSource(result.getInt("SOURCE"));
                move.setDestination(result.getInt("DESTINATION"));
                return move;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
