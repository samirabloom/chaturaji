package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.ai.MoveListener;
import ac.ic.chaturaji.model.GameStatus;
import ac.ic.chaturaji.model.Result;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author samirarabbanian
 */
public class NotifyPlayer implements MoveListener {
    private final String playerId;
    private final Map<String, Channel> clients;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();

    public NotifyPlayer(String playerId, Map<String, Channel> clientChannels) {
        this.playerId = playerId;
        this.clients = clientChannels;
    }

    @Override
    public void pieceMoved(Result result) {
        try {
            String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            logger.debug("Sending result [" + jsonResult + "] to player [ID_" + playerId + "]");
            Channel channel = clients.get("ID_" + playerId);
            if (channel != null) {
                channel.writeAndFlush(new TextWebSocketFrame(jsonResult));
            } else {
                logger.warn("No channel found for player [ID_" + playerId + "] therefore result [" + result + "] not being sent to that player");
            }
            if (result.getGameStatus() == GameStatus.GAME_OVER) {
                clients.remove("ID_" + playerId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
