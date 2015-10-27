package com.rpc.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务发现类
 * @author bailu-ds
 *
 */
public class ServiceDiscovery {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	private volatile List<String> dataList = new ArrayList<String>();
	
	private String registryAddress;
	
	public ServiceDiscovery(String registryAddress) {
		this.registryAddress = registryAddress;
		ZooKeeper zk =  connectServer();
		if (null != zk) {
			watchNode(zk);
		}
	}
	
//	private void initData() {
//		ZooKeeper zk =  connectServer();
//		if (null != zk) {
//			watchNode(zk);
//		}
//	}
	
	public String discover() {
		String data = null;
		int size = dataList.size();
//		if (0 == size) {
//			ZooKeeper zk =  connectServer();
//			if (null != zk) {
//				watchNode(zk);
//			}
//		}
		
		if (size > 0) {
			if (size == 1) {
				data = dataList.get(0);
				LOGGER.debug("using only data: {}", data);
			} else {
				data = dataList.get(ThreadLocalRandom.current().nextInt(size));
				LOGGER.debug("using only data: {}", data);
			}
		}
		return data;
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
		} catch (IOException e) {
			LOGGER.error("", e);
		} catch (InterruptedException e) {
			LOGGER.error("", e);
		}
		return zk;
	}
	
	/**
	 * 服务发现， 如果ZK 出现变动
	 * @param zk
	 */
	private void watchNode(final ZooKeeper zk) {
		try {
			List<String> nodeList = zk.getChildren(Constant.ZK_DATA_PATH, new Watcher() {
				public void process(WatchedEvent event) {
					if (event.getType() == Event.EventType.NodeChildrenChanged) {
						watchNode(zk);
					}
				}
			});
			
			List<String> dataList = new ArrayList<String>();
			for (String node : nodeList) {
				byte[] bytes = zk.getData(Constant.ZK_DATA_PATH + "/" + node, false, null);
				dataList.add(new String(bytes));
			}
			LOGGER.debug("node data: {}", dataList);
			this.dataList = dataList;
		} catch (KeeperException e) {
			LOGGER.debug("", e);
		} catch (InterruptedException e) {
			LOGGER.debug("", e);
		}
	}
}
