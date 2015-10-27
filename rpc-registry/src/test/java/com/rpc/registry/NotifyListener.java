package com.rpc.registry;

import java.util.EventListener;

import org.apache.zookeeper.Watcher.Event.EventType;

public interface NotifyListener extends EventListener {

	void change(EventType type);
	
}