package ac.ic.chaturaji.dao;

import ac.ic.chaturaji.model.Game;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author samirarabbanian
 */
@Component
public class GameDAO {

    public List<Game> getAll() {
        List<Game> games = new ArrayList<>();
        games.add(new Game("1"));
        games.add(new Game("2"));
        games.add(new Game("3"));
        games.add(new Game("4"));

        return games;
    }

}
