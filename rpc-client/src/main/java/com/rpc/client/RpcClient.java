package com.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rpc.common.RpcDecoder;
import com.rpc.common.RpcEncoder;
import com.rpc.common.RpcRequest;
import com.rpc.common.RpcResponse;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);
	
	/** server */
	private String host;
	
	/** 端口 */
	private int port;
	
	private RpcResponse response;
	
	private final Object obj = new Object();
	
	public RpcClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
		this.response = response;
		synchronized (obj) {
			obj.notifyAll();
		}
	}

	/**
	 * 异常时关闭通道
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOGGER.error("client caught exception", cause);
		ctx.close();
	}
	
	/**
	 * NIO 通信
	 * @param request
	 * @return
	 * @throws InterruptedException
	 */
	public RpcResponse send(RpcRequest request) throws InterruptedException {
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)
			.channel(NioSocketChannel.class)
			.handler(new ChannelInitializer<SocketChannel>(){
				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline()
					.addLast(new RpcEncoder(RpcRequest.class))		//编码
					.addLast(new RpcDecoder(RpcResponse.class))		//解码
					.addLast(RpcClient.this);
				}
			})
			.option(ChannelOption.SO_KEEPALIVE, true);				//长连接
			
			ChannelFuture future = bootstrap.connect(host, port).sync(); 	//同步连接服务器
			future.channel().writeAndFlush(request).sync();					//同步发送请求
			
			synchronized (obj) {
				obj.wait();
			}
			
			if (null != response) {
				future.channel().closeFuture().sync();
			}
			return response;
		} finally {
			group.shutdownGracefully();
		}
	}
}
