package ac.ic.chaturaji.web.websockets;

import ac.ic.chaturaji.dao.MoveDAO;
import ac.ic.chaturaji.model.Move;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.websockets.ClientRegistrationListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author samirarabbanian
 */
public class ReplayGameMoveSender implements ClientRegistrationListener {
    public static final int MOVE_DELAY = 500;
    private final String gameId;
    private final MoveDAO moveDAO;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();

    public ReplayGameMoveSender(String gameId, MoveDAO moveDAO) {
        this.gameId = gameId;
        this.moveDAO = moveDAO;
    }

    @Override
    public void onRegister(Channel channel) {
        try {
            for (Move move : moveDAO.getAll(gameId)) {
                String jsonMove = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(move);
                logger.debug("Sending result [" + jsonMove + "] for game [" + gameId + "]");
                channel.writeAndFlush(new TextWebSocketFrame(jsonMove));
                TimeUnit.MILLISECONDS.sleep(MOVE_DELAY);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
