package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.Game;
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
                new Game(UUID.randomUUID().toString()),
                new Game(UUID.randomUUID().toString()),
                new Game(UUID.randomUUID().toString()),
                new Game(UUID.randomUUID().toString())
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
        games.put(game.getId(), game);
    }
}
