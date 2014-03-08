package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.*;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

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

    @Resource
    UserDAO userDAO;

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
    }

    public void saveMove(Move move, Game game){
        String sql = "SELECT MAX(MOVE_ID) AS PREVIOUS_ID FROM MOVE GROUP BY GAME_ID HAVING GAME_ID=?";

        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, game.getId());
            ResultSet result = ps.executeQuery();
            int previous_id = 0;
            while (result.next()) {
                previous_id = result.getInt("PREVIOUS_ID");
            }

            sql = "INSERT INTO MOVE(MOVE_ID,GAME_ID,SOURCE,DESTINATION) VALUES (?,?,?,?)";

            ps = connection.prepareStatement(sql);

            ps.setInt(1, previous_id + 1);
            ps.setString(2, game.getId());
            ps.setInt(3, move.getSource());
            ps.setInt(4, move.getDestination());
            int i = ps.executeUpdate();
            if (i != 1) {
                throw new RuntimeException();
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Move getMove(Game game, int move_no){
        String sql = "SELECT * FROM MOVE WHERE GAME_ID=?";

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, game.getId());
            ResultSet result = ps.executeQuery();
            Move move = null;
            result.next();
            move = new Move();
            move.setColour(Colour.values()[move_no%4-1]);
            move.setSource(result.getInt("SOURCE"));
            move.setDestination(result.getInt("DESTINATION"));
            return move;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Move> getAllMoves(Game game){
        String sql = "SELECT * FROM MOVE WHERE GAME_ID=?";
        ArrayList<Move> Moves = new ArrayList<>();
        try  {
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, game.getId());
            ResultSet result = ps.executeQuery();
            Move move = null;
            while(result.next()){
                move = new Move();

                move.setColour(Colour.values()[(result.getInt("MOVE_ID")-1)%4]);
                move.setSource(result.getInt("SOURCE"));
                move.setDestination(result.getInt("DESTINATION"));
                Moves.add(move);
            }
            return Moves;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePlayers(Game game, List<Player>  Players){
        String sql = "INSERT INTO PLAYER(PLAYER_ID,GAME_ID, USER_ID,COLOUR,TYPE) VALUES (?,?,?,?,?)";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            for(Player player: Players){
                ps.setString(1,player.getId());
                ps.setString(2, game.getId());
                ps.setString(3, player.getUser().getId());
                ps.setInt(4, player.getColour().ordinal());
                if(player.getType() == PlayerType.AI)
                    ps.setString(5,"AI");
                else
                    ps.setString(5,"HUMAN");
                int i = ps.executeUpdate();
                if (i != 1) {
                    throw new RuntimeException();
                }
            }
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Player> getPlayers(Game game){
        String sql = "SELECT * FROM PLAYER WHERE GAME_ID=?";
        List<Player> Players = new ArrayList<Player>();
        try  {
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, game.getId());
            ResultSet result = ps.executeQuery();
            Player player = null;
            while(result.next()){
                player = new Player();
                String id = result.getString("PLAYER_ID");
                player.setId(id);
                player.setUser(userDAO.get(result.getString("USER_ID")));
                player.setColour(Colour.values()[result.getInt("COLOUR")]);
                if(result.getString("TYPE") == "AI")
                    player.setType(PlayerType.AI);
                else
                    player.setType(PlayerType.HUMAN);
                Players.add(player);
            }
            return Players;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
