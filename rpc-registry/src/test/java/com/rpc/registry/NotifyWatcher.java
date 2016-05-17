package com.rpc.registry;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class NotifyWatcher implements Watcher{

	private NotifyListener notify;
	
	
	public void process(WatchedEvent event) {
		notify.change(event.getType());
	}


	public NotifyListener getNotify() {
		return notify;
	}


	public void setNotify(NotifyListener notify) {
		this.notify = notify;
	}


	public NotifyWatcher(NotifyListener notify) {
		this.notify = notify;
	}
	
	

}
