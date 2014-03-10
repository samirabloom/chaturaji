package ac.ic.chaturaji.websockets;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;

import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;

/**
 * Handles handshakes and messages
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    public final static String WEB_SOCKET_PATH = "movelistener";
    private final Map<String, Channel> clients;
    private WebSocketServerHandshaker handshaker;

    public WebSocketServerHandler(Map<String, Channel> clients) {
        this.clients = clients;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // socket handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://" + req.headers().get(HOST) + "/" + WEB_SOCKET_PATH, null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {

        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        // store client channel against client id
        String request = ((TextWebSocketFrame) frame).text();
        if (request.startsWith("ID")) {
            clients.put(request, ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
