package com.rpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import com.rpc.common.RpcRequest;
import com.rpc.common.RpcResponse;
import com.rpc.registry.ServiceDiscovery;

/**
 * Rpc 代理
 * @author bailu-ds
 *
 */
public class RpcProxy {

	/** 服务地址 */
	private String serverAddress;
	
	/** 服务发现 */
	private ServiceDiscovery serviceDiscovery;

	public RpcProxy(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	public RpcProxy(ServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}
	
	/**
	 * 创建代理类
	 * @param interfaceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T create (Class<?> interfaceClass) {
		return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				
				//请求参数封装
				RpcRequest request = new RpcRequest();
				request.setRequestId(UUID.randomUUID().toString());
				request.setClassName(method.getDeclaringClass().getName());
				request.setMethodName(method.getName());
				request.setParameterTypes(method.getParameterTypes());
				request.setParameters(args);
				
				//服务发现, ZK中取得服务地址
				if (null != serviceDiscovery) {
					serverAddress = serviceDiscovery.discover(method.getDeclaringClass().getName()); //发现服务
				}
				
				String[] array = serverAddress.split(":");
				String host = array[0];
				int port = Integer.parseInt(array[1]);
				
				RpcClient client = new RpcClient(host, port);
				RpcResponse response = client.send(request);
				
				if (response.isError()) {
					throw response.getError();
				} else {
					return response.getResult();
				}
			}
		});
	}
}
