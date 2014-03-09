package ac.ic.chaturaji.websockets;

import ac.ic.chaturaji.model.Result;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.codehaus.jackson.map.ObjectMapper;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private final WebSocketClientHandshaker handshaker;
    private final GameMoveListener gameMoveListener;
    private final String playerId;
    private ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker, GameMoveListener gameMoveListener, String playerId) {
        this.handshaker = handshaker;
        this.gameMoveListener = gameMoveListener;
        this.playerId = playerId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();

        // complete handshake
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(channel, (FullHttpResponse) msg);

            channel.writeAndFlush(new TextWebSocketFrame(playerId));
            for (int i = 0; i < 10; i++) {
                channel.writeAndFlush(new TextWebSocketFrame("Client " + playerId + " to server message #" + i));
            }
            return;
        }

        // handle server messages
        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            // todo remove the system out debugging line below
            System.out.println("WebSocket Client " + playerId + " received message: " + textFrame.text());
            Result result = objectMapper.readValue(textFrame.text(), Result.class);
            gameMoveListener.onMoveCompleted(result);
        } else if (frame instanceof CloseWebSocketFrame) {
            // todo remove the system out debugging
            System.out.println("WebSocket Client " + playerId + " received closing");
            channel.close();
        } else {
            throw new RuntimeException("Unexpected response [" + msg + "]");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
