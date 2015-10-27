package com.rpc.registry;

public class Constant {

	/** ZK Session 超时时间 */
	public final static int ZK_SESSION_TIMEOUT = 5000;
	
	/** ZK 存放数据的持久化目录 */
	public final static String ZK_REGISTRY_PATH = "/registry";
	
	/** ZK 数据存放地址 */
	public final static String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";
	
}
