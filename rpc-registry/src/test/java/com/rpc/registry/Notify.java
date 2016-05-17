package com.rpc.registry;

import org.apache.zookeeper.Watcher.Event.EventType;

public class Notify implements NotifyListener {

	public void change(EventType type) {
		System.out.println("watch:" + type.getIntValue());
	}

	
	
}
