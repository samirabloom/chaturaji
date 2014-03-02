package ac.ic.chaturaji.integration;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private final WebSocketClientHandshaker handshaker;
    private final String clientId;

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker, String clientId) {
        this.handshaker = handshaker;
        this.clientId = clientId;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();

        // complete handshake
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(channel, (FullHttpResponse) msg);

            channel.writeAndFlush(new TextWebSocketFrame(clientId));
            for (int i = 0; i < 10; i++) {
                channel.writeAndFlush(new TextWebSocketFrame("Client " + clientId + " to server message #" + i));
            }
            return;
        }

        // handle server messages
        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            System.out.println("WebSocket Client " + clientId + " received message: " + textFrame.text());
        } else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("WebSocket Client " + clientId + " received closing");
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
