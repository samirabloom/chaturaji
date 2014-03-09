package ac.ic.chaturaji.websockets;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author samirarabbanian
 */
public class WebSocketsServer {

    private final Map<String, Channel> clients = new ConcurrentHashMap<>();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void startServer(final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bossGroup = new NioEventLoopGroup(1);
                workerGroup = new NioEventLoopGroup();
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

                    bootstrap.bind(port).sync().channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    stopServer();
                }
            }
        }).start();
    }

    public void stopServer() {
        if (bossGroup != null && workerGroup != null) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public Map<String, Channel> getClients() {
        return clients;
    }
}
