package heast.chatserver;

import heast.chatserver.network.Database;
import heast.chatserver.network.ServerChatHandler;
import heast.chatserver.network.ServerNetwork;
import heast.core.network.*;
import heast.core.security.AES;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Arrays;

public final class Server {

    public static void main(String... args){
/*        //test aes
        String s1= "Hello World _ - 0987654321!/&%$$%&/()=";
        byte[] encrypted=AES.INSTANCE.encrypt(s1.getBytes(),"xyz".getBytes());
        byte[] decrypted=AES.INSTANCE.decrypt(encrypted, "xyz".getBytes());
        String s2= new String(decrypted);
        System.out.println(s2);
*/
        int port = args.length > 0
                ? Integer.parseInt(args[0])
                : 6969;

        start(port);
    }

    public static void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new ChannelInitializer<>() {
                        @Override
                        public void initChannel(Channel ch) {
                            ch.pipeline()
                                    .addLast(new ChannelHandlerAdapter() {
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) {
                                            ServerNetwork.initialize();
                                            Database.initialize();
                                            System.out.println("Server active!");
                                        }

                                        @Override
                                        public void channelInactive(ChannelHandlerContext ctx) {
                                            System.out.println("Server shutdown!");
                                        }
                                    });
                        }
                    })
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ClientConnection connection = new ClientConnection(NetworkSide.SERVER, NetworkState.CHAT);
                            connection.setListener(new ServerChatHandler(connection));
                            ch.pipeline()
                                    .addLast(new PacketDecoder(NetworkSide.CLIENT))
                                    .addLast(new PacketEncoder(NetworkSide.SERVER))
                                    .addLast(connection);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .bind(port)
                    .syncUninterruptibly()

                    .channel()
                    .closeFuture()
                    .syncUninterruptibly();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}