package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.ai.AI;
import ac.ic.chaturaji.ai.MoveListener;
import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.Result;
import ac.ic.chaturaji.model.User;
import ac.ic.chaturaji.security.SpringSecurityUserContext;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author samirarabbanian
 */
@Controller
public class GameController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private GameDAO gameDAO;
    @Resource
    private AI ai;
    @Resource
    private SpringSecurityUserContext springSecurityUserContext;
    private ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // ignore failures
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        // relax parsing
        objectMapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // use arrays
        objectMapper.configure(DeserializationConfig.Feature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        // remove empty values from JSON
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);

        return objectMapper;
    }

    @ResponseBody
    @RequestMapping(value = "/games", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String getGameList() throws IOException {
        User currentUser = springSecurityUserContext.getCurrentUser();
        List<Game> notYourGames = new ArrayList<>();
        for (Game game : gameDAO.getAll()) {
            if (!alreadyJoinedGame(game, currentUser)) {
                notYourGames.add(game);
            }
        }
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(notYourGames);
    }

    @ResponseBody
    @RequestMapping(value = "/game", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> createGame(@RequestParam("numberOfAIPlayers") int numberOfAIPlayers) throws IOException {
        if (numberOfAIPlayers < 0 || numberOfAIPlayers > 3) {
            return new ResponseEntity<>("Invalid numberOfAIPlayers: " + numberOfAIPlayers + " is not between 0 and 3 inclusive", HttpStatus.BAD_REQUEST);
        }
        User currentUser = springSecurityUserContext.getCurrentUser();
        try {
            Game game = new Game(UUID.randomUUID().toString(), new Player(currentUser));
            gameDAO.save(game);
            ai.createGame(game);
            ai.registerListener(game.getId(), new MoveListener() {
                @Override
                public void pieceMoved(Result result) {
                    System.out.println("result = " + result);
                }
            });

        } catch (Exception e) {
            logger.warn("Exception while saving game", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    @ResponseBody
    @RequestMapping(value = "/joinGame", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity<String> joinGame(@RequestParam("gameId") String gameId) throws IOException {
        Game game = gameDAO.get(gameId);
        if (game == null) {
            return new ResponseEntity<>("No game found with gameId: " + gameId, HttpStatus.BAD_REQUEST);
        }
        if (game.getPlayers().size() >= 4) {
            return new ResponseEntity<>("Game already has four players", HttpStatus.BAD_REQUEST);
        }
        User currentUser = springSecurityUserContext.getCurrentUser();
        if (alreadyJoinedGame(game, currentUser)) {
            return new ResponseEntity<>("You have already joined this game", HttpStatus.BAD_REQUEST);
        }

        game.addPlayer(new Player(currentUser));
        try {
            gameDAO.save(game);
            ai.registerListener(gameId, new MoveListener() {
                @Override
                public void pieceMoved(Result result) {
                    System.out.println("result = " + result);
                }
            });

        } catch (Exception e) {
            logger.warn("Exception while saving game", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("", HttpStatus.CREATED);
    }

    public boolean alreadyJoinedGame(Game game, User user) {
        if (user != null) {
            for (Player player : game.getPlayers()) {
                if (player.getUser().equals(user)) {
                    return true;
                }
            }
        }
        return false;
    }


}
