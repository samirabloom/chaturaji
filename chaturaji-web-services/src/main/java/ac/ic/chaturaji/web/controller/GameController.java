package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.ai.AI;
import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.dao.MoveDAO;
import ac.ic.chaturaji.dao.PlayerDAO;
import ac.ic.chaturaji.model.*;
import ac.ic.chaturaji.security.SpringSecurityUserContext;
import ac.ic.chaturaji.uuid.UUIDFactory;
import ac.ic.chaturaji.web.websockets.NotifyPlayer;
import ac.ic.chaturaji.web.websockets.ReplayGameMoveSender;
import ac.ic.chaturaji.web.websockets.WebSocketServletContextListener;
import ac.ic.chaturaji.websockets.ClientRegistrationListener;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static ac.ic.chaturaji.web.controller.InMemoryGamesContextListener.getInMemoryGames;

/**
 * @author samirarabbanian
 */
@Controller
public class GameController {
    public static final int AI_PLAYER_DELAY = 500;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private GameDAO gameDAO;
    @Resource
    private MoveDAO moveDAO;
    @Resource
    private PlayerDAO playerDAO;
    @Resource
    private UUIDFactory uuidFactory;
    @Resource
    private AI ai;
    @Resource
    private SpringSecurityUserContext springSecurityUserContext;
    @Resource
    private ServletContext servletContext;
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @ResponseBody
    @RequestMapping(value = "/games", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public List<Game> getGameList() throws IOException {
        User currentUser = springSecurityUserContext.getCurrentUser();
        List<Game> notYourGames = new ArrayList<>();
        for (Game game : gameDAO.getAllWaitingForPlayers()) {
            if (!alreadyJoinedGame(game, currentUser)) {
                notYourGames.add(game);
            }
        }
        return notYourGames;
    }

    @ResponseBody
    @RequestMapping(value = "/createGame", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity createGame(@RequestParam("numberOfAIPlayers") int numberOfAIPlayers) throws IOException {
        if (numberOfAIPlayers < 0 || numberOfAIPlayers > 3) {
            return new ResponseEntity<>("Invalid numberOfAIPlayers: " + numberOfAIPlayers + " is not between 0 and 3 inclusive", HttpStatus.BAD_REQUEST);
        }
        User currentUser = springSecurityUserContext.getCurrentUser();
        // create new player
        Player player = new Player(uuidFactory.generateUUID(), currentUser, Colour.values()[0], PlayerType.HUMAN);
        // create game
        Game game = new Game(uuidFactory.generateUUID(), player);
        // add AI players
        for (int i = 1; i <= numberOfAIPlayers; i++) {
            game.addPlayer(new Player(uuidFactory.generateUUID(), currentUser, Colour.values()[i], PlayerType.AI));
        }
        ai.createGame(game);
        try {
            // save game
            gameDAO.save(game);
            getInMemoryGames(servletContext).put(game.getId(), game);
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
        Game game = getInMemoryGames(servletContext).get(gameId);
        if (game == null) {
            game = gameDAO.get(gameId);
            if (game == null) {
                return new ResponseEntity<>("No game found with gameId: " + gameId, HttpStatus.BAD_REQUEST);
            } else {
                getInMemoryGames(servletContext).put(gameId, game);
            }
        }
        if (game.getPlayers().size() >= 4) {
            return new ResponseEntity<>("Game already has four players", HttpStatus.BAD_REQUEST);
        }
        User currentUser = springSecurityUserContext.getCurrentUser();
        if (alreadyJoinedGame(game, currentUser)) {
            return new ResponseEntity<>("You have already joined this game", HttpStatus.BAD_REQUEST);
        }

        // create new player
        Player player = new Player(uuidFactory.generateUUID(), currentUser, Colour.values()[game.getPlayerCount()], PlayerType.HUMAN);
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
    public ResponseEntity<String> submitMove(@RequestBody final Move move) {
        final Game game = getInMemoryGames(servletContext).get(move.getGameId());
        if (game != null) {
            Result result = ai.submitMove(game, move);
            for (Player player : result.getGame().getPlayers()) {
                playerDAO.save(result.getGame().getId(), player);
            }
            moveDAO.save(result.getGame().getId(), result.getMove());
            // now schedule AI move if next player is AI
            if (game.getNextPlayer().getType() == PlayerType.AI) {
                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.MILLISECONDS.sleep(AI_PLAYER_DELAY);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        submitMove(new Move(uuidFactory.generateUUID(), move.getGameId(), game.getNextPlayerColour(), -1, -1));
                    }
                });
            }
            return new ResponseEntity<>("", HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>("No game found with gameId: " + move.getGameId(), HttpStatus.BAD_REQUEST);
        }
    }


    @ResponseBody
    @RequestMapping(value = "/replayGame", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public ResponseEntity replayGame(@RequestParam("gameId") String gameId) throws IOException {
        Game game = getInMemoryGames(servletContext).get(gameId);
        if (game == null) {
            return new ResponseEntity<>("No game found with gameId: " + gameId, HttpStatus.BAD_REQUEST);
        }
        // find player
        Player player = findPlayer(game, springSecurityUserContext.getCurrentUser());
        if (player == null) {
            return new ResponseEntity<>("You can only replay games for which you were a player", HttpStatus.BAD_REQUEST);
        }

        // add web socket registration listener
        Map<String, ClientRegistrationListener> clientRegistrationListeners = (Map<String, ClientRegistrationListener>) servletContext.getAttribute(WebSocketServletContextListener.WEB_SOCKET_CLIENT_REGISTRATION_LISTENERS_ATTRIBUTE_NAME);
        clientRegistrationListeners.put("ID_" + player.getId(), new ReplayGameMoveSender(gameId, moveDAO));

        return new ResponseEntity<>(player, HttpStatus.ACCEPTED);
    }

    public Player findPlayer(Game game, User user) {
        if (user != null) {
            for (Player player : game.getPlayers()) {
                if (player.getUser().equals(user)) {
                    return player;
                }
            }
        }
        return null;
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
