package com.rpc.registry;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class NotifyTest {

	private static String registryAddress = "127.0.0.1:2181";
	
	public static ZooKeeper connectServer() {
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(registryAddress, Constants.ZK_SESSION_TIMEOUT, new Watcher() {
				
				public void process(WatchedEvent event) {
					System.out.println("wactch : connected");
					
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return zk;
	}
	
	public static void main(String[] args) {
		ZooKeeper zk = connectServer();
//		String data = "{hu:01}";
//		exitsNode(zk, "/reg");
//		createNode(zk, data, "/reg");
//		exitsNode(zk, "/reg");
//		chageNode(zk, data);
//		getClNode(zk, "/reg");
//		createNode(zk, data, "/reg/r");
//		getClNode(zk, "/reg");
//		deleteNode(zk, "/reg");
//		getClNode(zk, "/reg");
	}
	
	private static void createNode(ZooKeeper zk, String data, String path1) {
		try {
			byte[] bytes = data.getBytes();
			
			String path = zk.create(path1, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			System.out.println(path);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void chageNode(ZooKeeper zk, String data) {
		try {
			byte[] bytes = data.getBytes();
			Stat path = zk.setData("/reg", bytes, -1);
			System.out.println(path);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void deleteNode(ZooKeeper zk, String path) {
		try {
			zk.delete(path, -1);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void getClNode(ZooKeeper zk, String path) {
		NotifyListener notify = new Notify();
		try {
			List<String> list = zk.getChildren("/reg", new NotifyWatcher(notify));
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static void exitsNode(ZooKeeper zk, String path) {
		NotifyListener notify = new Notify();
		try {
			Stat list = zk.exists(path, new NotifyWatcher(notify));
			System.out.println(path);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
