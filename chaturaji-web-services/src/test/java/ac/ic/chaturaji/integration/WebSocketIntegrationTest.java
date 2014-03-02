package ac.ic.chaturaji.integration;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author samirarabbanian
 */
@Ignore
public class WebSocketIntegrationTest {

    private static final Map<String, Channel> clients = new HashMap<>();

    // @BeforeClass
    public static void runServer() throws InterruptedException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int port = 8080;
                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap bootstrap = new ServerBootstrap()
                            .group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    ChannelPipeline pipeline = socketChannel.pipeline();
                                    pipeline.addLast("codec-http", new HttpServerCodec());
                                    pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                                    pipeline.addLast("handler", new WebSocketServerHandler(clients));
                                }
                            });

                    Channel channel = bootstrap.bind(port).sync().channel();
                    System.out.println("Web socket server started at port " + port + '.');

                    channel.closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("Stopping Server");
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        }).start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    if (clients.get("ID_1") != null) {
                        for (int i = 0; i < 10; i++) {
                            clients.get("ID_1").writeAndFlush(new TextWebSocketFrame("Message #" + i));
                        }
                    }
                    if (clients.get("ID_2") != null) {
                        for (int i = 0; i < 10; i++) {
                            clients.get("ID_2").writeAndFlush(new TextWebSocketFrame("Message #" + i));
                        }
                    }
                    if (clients.get("ID_3") != null) {
                        for (int i = 0; i < 10; i++) {
                            clients.get("ID_3").writeAndFlush(new TextWebSocketFrame("Message #" + i));
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Test
    public void shouldSupportWebSocketConnection() throws InterruptedException, URISyntaxException {

        URI uri = new URI("ws://localhost:8080/websocket");

        createClient(uri, "ID_1");
        createClient(uri, "ID_2");
        createClient(uri, "ID_3");

        TimeUnit.HOURS.sleep(1);
    }

    private void createClient(final URI uri, final String clientId) {
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

                    final WebSocketClientHandler handler = new WebSocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, null), clientId);

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
