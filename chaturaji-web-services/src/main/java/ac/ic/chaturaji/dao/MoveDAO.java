package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Move;
import org.joda.time.LocalDateTime;
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
            String sql = "INSERT INTO MOVE(MOVE_ID, CREATED_DATE, GAME_ID, COLOUR, SOURCE, DESTINATION, YELLOW_SCORE, BLUE_SCORE, RED_SCORE, GREEN_SCORE) VALUES (?,?,?,?,?,?,?,?,?,?)";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, move.getId());
                ps.setTimestamp(2, new java.sql.Timestamp(move.getCreatedDate().toDate().getTime()));
                ps.setString(3, gameId);
                ps.setInt(4, move.getColour().ordinal());
                ps.setInt(5, move.getSource());
                ps.setInt(6, move.getDestination());
                ps.setInt(7, move.getYellowScore());
                ps.setInt(8, move.getBlueScore());
                ps.setInt(9, move.getRedScore());
                ps.setInt(10, move.getGreenScore());
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException();
                }
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String sql = "UPDATE MOVE SET GAME_ID=?, CREATED_DATE=?, COLOUR=?, SOURCE=?, DESTINATION=?, YELLOW_SCORE=?, BLUE_SCORE=?, RED_SCORE=?, GREEN_SCORE=? WHERE MOVE_ID=?";
            try (Connection connection = dataSource.getConnection()) {
                PreparedStatement ps = connection.prepareStatement(sql);

                ps.setString(1, gameId);
                ps.setTimestamp(2, new java.sql.Timestamp(move.getCreatedDate().toDate().getTime()));
                ps.setInt(3, move.getColour().ordinal());
                ps.setInt(4, move.getSource());
                ps.setInt(5, move.getDestination());
                ps.setInt(6, move.getYellowScore());
                ps.setInt(7, move.getBlueScore());
                ps.setInt(8, move.getRedScore());
                ps.setInt(9, move.getGreenScore());
                ps.setString(10, move.getId());
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
        String sql = "SELECT MOVE_ID, GAME_ID, CREATED_DATE, COLOUR, SOURCE, DESTINATION, YELLOW_SCORE, BLUE_SCORE, RED_SCORE, GREEN_SCORE FROM MOVE WHERE GAME_ID=? ORDER BY ORDER_ID ASC";
        ArrayList<Move> moves = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, gameId);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                Move move = new Move();

                move.setId(result.getString("MOVE_ID"));
                move.setGameId(result.getString("GAME_ID"));
                move.setCreatedDate(new LocalDateTime(result.getTimestamp("CREATED_DATE").getTime()));
                move.setColour(Colour.values()[result.getInt("COLOUR")]);
                move.setSource(result.getInt("SOURCE"));
                move.setDestination(result.getInt("DESTINATION"));
                move.setYellowScore(result.getInt("YELLOW_SCORE"));
                move.setBlueScore(result.getInt("BLUE_SCORE"));
                move.setRedScore(result.getInt("RED_SCORE"));
                move.setGreenScore(result.getInt("GREEN_SCORE"));
                moves.add(move);
            }
            return moves;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Move get(String moveId) {
        String sql = "SELECT MOVE_ID, GAME_ID, CREATED_DATE, COLOUR, SOURCE, DESTINATION, YELLOW_SCORE, BLUE_SCORE, RED_SCORE, GREEN_SCORE FROM MOVE WHERE MOVE_ID=?";
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, moveId);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                Move move = new Move();

                move.setId(result.getString("MOVE_ID"));
                move.setGameId(result.getString("GAME_ID"));
                move.setCreatedDate(new LocalDateTime(result.getTimestamp("CREATED_DATE").getTime()));
                move.setColour(Colour.values()[result.getInt("COLOUR")]);
                move.setSource(result.getInt("SOURCE"));
                move.setDestination(result.getInt("DESTINATION"));
                move.setYellowScore(result.getInt("YELLOW_SCORE"));
                move.setBlueScore(result.getInt("BLUE_SCORE"));
                move.setRedScore(result.getInt("RED_SCORE"));
                move.setGreenScore(result.getInt("GREEN_SCORE"));
                return move;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
