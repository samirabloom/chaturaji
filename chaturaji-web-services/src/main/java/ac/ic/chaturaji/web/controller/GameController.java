package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.ai.AI;
import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Move;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.User;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.security.SpringSecurityUserContext;
import ac.ic.chaturaji.web.websockets.WebSocketServletContextListener;
import io.netty.channel.Channel;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author samirarabbanian
 */
@Controller
public class GameController {
    private static final String GAME_ATTRIBUTE_NAME = "GAME_ATTRIBUTE_NAME";
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private GameDAO gameDAO;
    @Resource
    private AI ai;
    @Resource
    private SpringSecurityUserContext springSecurityUserContext;
    @Resource
    private ServletContext servletContext;
    private ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();
    private Map<String, Game> games = new ConcurrentHashMap<>();

    @PostConstruct
    public void addGameMapToServletContext() {
        servletContext.setAttribute(GAME_ATTRIBUTE_NAME, games);
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
            // create new player
            Player player = new Player(UUID.randomUUID().toString(), currentUser);
            // create game and save
            Game game = new Game(UUID.randomUUID().toString(), player);
            ai.createGame(game);
            gameDAO.save(game);
            games.put(game.getId(), game);
            // register web socket listener
            NotifyPlayer moveListener = new NotifyPlayer(player.getId(), (Map<String, Channel>) servletContext.getAttribute(WebSocketServletContextListener.WEB_SOCKET_CLIENT_ATTRIBUTE_NAME));
            ai.registerListener(game.getId(), moveListener);
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

        // create new player
        Player player = new Player(UUID.randomUUID().toString(), currentUser);
        try {
            // update game and save
            game.addPlayer(player);
            gameDAO.save(game);
            // register web socket listener
            NotifyPlayer moveListener = new NotifyPlayer(player.getId(), (Map<String, Channel>) servletContext.getAttribute(WebSocketServletContextListener.WEB_SOCKET_CLIENT_ATTRIBUTE_NAME));
            ai.registerListener(gameId, moveListener);
        } catch (Exception e) {
            logger.warn("Exception while saving game", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(player), HttpStatus.CREATED);
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

    @ResponseBody
    @RequestMapping(value = "/submitMove", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public void submitMove(Move move) {
        // make sure we set id of move object
    }

}
