package com.rpc.server.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.rpc.common.RpcDecoder;
import com.rpc.common.RpcEncoder;
import com.rpc.common.RpcRequest;
import com.rpc.common.RpcResponse;
import com.rpc.server.RpcHandler;
import com.rpc.server.RpcServer;

public class NettyUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
	
	public static int PORT = 8000;
	
	public static void createServer(final int port, final Map<String, Object> handlerMap) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline()
					.addLast(new RpcDecoder(RpcRequest.class))
					.addLast(new RpcEncoder(RpcResponse.class))
					.addLast(new RpcHandler(handlerMap));
				}
			})
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			ChannelFuture future = bootstrap.bind(IpUtil.getIp(), port).sync();
			LOGGER.debug("server started on port {}", port);
//			future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
}
