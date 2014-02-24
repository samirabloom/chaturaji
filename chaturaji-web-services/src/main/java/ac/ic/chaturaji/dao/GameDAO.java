package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.User;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author samirarabbanian
 */
@Component
public class GameDAO {

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
    }

    public void save(Game game) {
        if (games.containsKey(game.getId())) {
            games.remove(game.getId());
        }
        games.put(game.getId(), game);
    }
}
