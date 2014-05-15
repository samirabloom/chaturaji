package ac.ic.chaturaji.web.websockets;

import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.dao.MoveDAO;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.GameStatus;
import ac.ic.chaturaji.model.Move;
import ac.ic.chaturaji.model.Result;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.websockets.ClientRegistrationListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author samirarabbanian
 */
public class ReplayGameMoveSender implements ClientRegistrationListener {
    public static final int MOVE_DELAY = 500;
    private final String gameId;
    private final MoveDAO moveDAO;
    private final GameDAO gameDAO;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();

    public ReplayGameMoveSender(String gameId, MoveDAO moveDAO, GameDAO gameDAO) {
        this.gameId = gameId;
        this.moveDAO = moveDAO;
        this.gameDAO = gameDAO;
    }

    @Override
    public void onRegister(Channel channel) {
        try {
            List<Move> all = moveDAO.getAll(gameId);
            Game game = gameDAO.get(gameId);
            for (int i = 0; i < all.size(); i++) {
                Move move = all.get(i);
                GameStatus gameStatus = null;
                if (i == all.size() - 1) {
                    gameStatus = GameStatus.GAME_OVER;
                } else {
                    gameStatus = GameStatus.IN_PLAY;
                }
                game.setGameStatus(gameStatus);
                String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(new Result(gameStatus, game, move));
                logger.debug("Sending result [" + jsonResult + "] for game [" + gameId + "]");
                channel.writeAndFlush(new TextWebSocketFrame(jsonResult));
                TimeUnit.MILLISECONDS.sleep(MOVE_DELAY);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
