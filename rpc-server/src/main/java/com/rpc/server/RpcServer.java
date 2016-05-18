package com.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.rpc.common.RpcDecoder;
import com.rpc.common.RpcEncoder;
import com.rpc.common.RpcRequest;
import com.rpc.common.RpcResponse;
import com.rpc.registry.ServiceRegistry;
import com.rpc.server.util.IpUtil;

public class RpcServer implements ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
	
	private ServiceRegistry serviceRegistry;
	
	private Map<String, Object> handlerMap = new HashMap<String, Object>();
		
	public RpcServer(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	/**
	 * 容器在初始化的时候,自动注册ZK, 并和server 保持长连接
	 */
	public void afterPropertiesSet() throws Exception {
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
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
//					.addLast(new WriteTimeoutHandler(180, TimeUnit.SECONDS));
				}
			})
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
//			String[] array = serverAddress.split(":");
//			String host = array[0];
//			int port = Integer.parseInt(array[1]);
			
			int port = 8000;
			String server = IpUtil.getIp() + ":" + port;
			
			ChannelFuture future = bootstrap.bind(IpUtil.getIp(), port).sync();
			LOGGER.debug("server started on port {}", port);
			if (null != serviceRegistry) {
//				serviceRegistry.register(serverAddress);
				register(server);
			}
			future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	/**
	 * 缓存Rpc 类
	 */
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
		if (MapUtils.isNotEmpty(serviceBeanMap)) {
			for (Object serviceBean : serviceBeanMap.values()) {
				String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
				handlerMap.put(interfaceName, serviceBean);
			}
		}
	}
	
	private void register(String server) {
		Iterator<String> it = handlerMap.keySet().iterator();
		while (it.hasNext()) {
			serviceRegistry.register(it.next(), server);
		}
	}
	
	

}
