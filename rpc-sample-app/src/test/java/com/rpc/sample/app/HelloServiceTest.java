package com.rpc.sample.app;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rpc.client.RpcProxy;
import com.rpc.sample.client.HelloService;
import com.rpc.sample.client.Person;

@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class HelloServiceTest {

	@Autowired
	private RpcProxy rpcProxy;
	
	@Test
	public void helloTest1() {
		HelloService hs = rpcProxy.create(HelloService.class);
		String result = hs.hello("lilei");
		Assert.assertEquals("Hello lilei", result);
	}
	
	@Test
	public void helloTest2() {
		HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello(new Person("Li", "Lei"));
        Assert.assertEquals("Hello firstName:Li lastName:Lei", result);
	}
	
}
