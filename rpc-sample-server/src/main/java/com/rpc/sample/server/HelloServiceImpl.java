package com.rpc.sample.server;

import com.rpc.sample.client.HelloService;
import com.rpc.sample.client.Person;
import com.rpc.server.RpcService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

	public String hello(String name) {
		return "Hello " + name;
	}

	public String hello(Person p) {
		return "Hello firstName:" + p.getFirstName() + " lastName:" + p.getLastName();
	}

}
