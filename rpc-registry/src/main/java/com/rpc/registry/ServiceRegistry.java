package com.rpc.registry;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务注册
 * @author bailu-ds
 *
 */
public class ServiceRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	private String registryAddress;
	
	public ServiceRegistry(String registryAddress) {
		this.registryAddress = registryAddress;
	}
	
//	/**
//	 * 注册服务
//	 * @param data
//	 */
//	public void register(String data) {
//		if (null != data) {
//			ZooKeeper zk = connectServer();
//			if (null != zk) {
//				createNode(zk, data);
//			}
//		}
//	}
	
	public void register(String node, String tmpNode) {
		if (null != node) {
			ZooKeeper zk = connectServer();
			if (null != zk) {
				try {
					if (null == zk.exists(Constant.ZK_REGISTRY_PATH + "/" + node, false)) {
						createNode(zk, node, null, CreateMode.PERSISTENT);
					}
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				createNode(zk, node + "/" + tmpNode, null, CreateMode.EPHEMERAL);
			}
		}
	}
	
	/**
	 * 连接ZK
	 * @return
	 */
	private ZooKeeper connectServer() {
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher(){
				public void process(WatchedEvent event) {
					if (event.getState() == Event.KeeperState.SyncConnected) {
						latch.countDown();
					}
				}
			});
			latch.await();
			createRootNode(zk);
		} catch (IOException e) {
			LOGGER.error("", e);
		} catch (InterruptedException e) {
			LOGGER.error("", e);
		} catch (KeeperException e) {
			LOGGER.error("", e);
		}
		return zk;
	}
	
//	/**
//	 * 创建节点
//	 * @param zk
//	 * @param data
//	 */
//	private void createNodeData(ZooKeeper zk, String data) {
//		try {
//			String path = zk.create(Constant.ZK_REGISTRY_PATH + "/", getDataBytes(null), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//			LOGGER.debug("create zookeeper node ({} => {})", path, getDataBytes(null));
//		} catch (KeeperException e) {
//			LOGGER.error("", e);
//		} catch (InterruptedException e) {
//			LOGGER.error("", e);
//		}
//	}
	
	private void createNode(ZooKeeper zk, String node, String data, CreateMode cm) {
		try {
			String path = zk.create(Constant.ZK_REGISTRY_PATH + "/" + node, getDataBytes(data), ZooDefs.Ids.OPEN_ACL_UNSAFE, cm);
			LOGGER.debug("create zookeeper node ({} => {})", path, getDataBytes(data));
		} catch (KeeperException e) {
			LOGGER.error("createNode ERROR:", e);
		} catch (InterruptedException e) {
			LOGGER.error("createNode ERROR:", e);
		}
	}
	
	private void createRootNode(ZooKeeper zk) throws KeeperException, InterruptedException {
		if (null == zk.exists(Constant.ZK_REGISTRY_PATH, false)) {
			zk.create(Constant.ZK_REGISTRY_PATH, getDataBytes(null), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
	}
	
	public byte[] getDataBytes(String data) {
		byte[] bytes = null;
		if (data != null) {
			bytes = data.getBytes();
		}
		return bytes;
	}
}
