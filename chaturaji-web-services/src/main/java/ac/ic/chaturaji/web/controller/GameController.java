package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.ai.AI;
import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.dao.PlayerDAO;
import ac.ic.chaturaji.model.*;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.security.SpringSecurityUserContext;
import ac.ic.chaturaji.web.websockets.NotifyPlayer;
import ac.ic.chaturaji.web.websockets.WebSocketServletContextListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    private PlayerDAO playerDAO;
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
    public List<Game> getGameList() throws IOException {
        User currentUser = springSecurityUserContext.getCurrentUser();
        List<Game> notYourGames = new ArrayList<>();
        for (Game game : gameDAO.getAll()) {
            if (!alreadyJoinedGame(game, currentUser)) {
                notYourGames.add(game);
            }
        }
        return notYourGames;
    }

    @ResponseBody
    @RequestMapping(value = "/game", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity createGame(@RequestParam("numberOfAIPlayers") int numberOfAIPlayers) throws IOException {
        if (numberOfAIPlayers < 0 || numberOfAIPlayers > 3) {
            return new ResponseEntity<>("Invalid numberOfAIPlayers: " + numberOfAIPlayers + " is not between 0 and 3 inclusive", HttpStatus.BAD_REQUEST);
        }
        User currentUser = springSecurityUserContext.getCurrentUser();
        // create new player
        Player player = new Player(UUID.randomUUID().toString(), currentUser, Colour.values()[0], PlayerType.HUMAN);
        // create game
        Game game = new Game(UUID.randomUUID().toString(), player);
        // add AI players
        for (int i = 1; i <= numberOfAIPlayers; i++) {
            game.addPlayer(new Player(UUID.randomUUID().toString(), currentUser, Colour.values()[i], PlayerType.AI));
        }
        ai.createGame(game);
        try {
            // save game
            gameDAO.save(game);
            games.put(game.getId(), game);
            // register web socket listener
            registerMoveListener(game.getId(), player);
        } catch (Exception e) {
            logger.warn("Exception while saving game", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(player, HttpStatus.CREATED);
    }

    @ResponseBody
    @RequestMapping(value = "/joinGame", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity joinGame(@RequestParam("gameId") String gameId) throws IOException {
        Game game = games.get(gameId);
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
        Player player = new Player(UUID.randomUUID().toString(), currentUser, Colour.values()[game.getPlayerCount()], PlayerType.HUMAN);
        try {
            // update game and save
            game.addPlayer(player);
            playerDAO.save(game.getId(), player);
            // register web socket listener
            registerMoveListener(gameId, player);
        } catch (Exception e) {
            logger.warn("Exception while saving game", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(player, HttpStatus.CREATED);
    }

    // Note: request must have a Content-Type: application/json; charset=UTF-8
    @ResponseBody
    @RequestMapping(value = "/submitMove", method = RequestMethod.POST, produces = "text/plain; charset=UTF-8")
    public ResponseEntity<String> submitMove(@RequestBody Move move) {
        ai.submitMove(games.get(move.getGameId()), move);
        return new ResponseEntity<>("", HttpStatus.ACCEPTED);
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

    private void registerMoveListener(String gameId, Player player) {
        ai.registerListener(gameId, new NotifyPlayer(player.getId(), (Map<String, Channel>) servletContext.getAttribute(WebSocketServletContextListener.WEB_SOCKET_CLIENT_ATTRIBUTE_NAME)));
    }

}
