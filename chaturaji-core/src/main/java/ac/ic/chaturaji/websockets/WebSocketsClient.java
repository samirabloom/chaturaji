package ac.ic.chaturaji.websockets;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author samirarabbanian
 */
public class WebSocketsClient {

    private final URI uri;

    public WebSocketsClient() {
        try {
            uri = new URI("ws://192.168.1.110:8080/movelistener");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerGameMoveListener(final GameMoveListener gameMoveListener, final String playerId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    Bootstrap bootstrap = new Bootstrap();
                    String protocol = uri.getScheme();
                    if (!"ws".equals(protocol)) {
                        throw new IllegalArgumentException("Unsupported protocol: " + protocol);
                    }

                    final WebSocketClientHandler handler = new WebSocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, null), gameMoveListener, playerId);

                    bootstrap.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline pipeline = ch.pipeline();
                                    pipeline.addLast("http-codec", new HttpClientCodec());
                                    pipeline.addLast("aggregator", new HttpObjectAggregator(8192));
                                    pipeline.addLast("ws-handler", handler);
                                }
                            });

                    Channel channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();

                    channel.closeFuture().sync();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    group.shutdownGracefully();
                }
            }
        }).start();
    }

}
