package com.itzixi.curator;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;

public class CuratorOperator {

	private CuratorFramework client = null;
	public static final String zkServerPath = "192.168.1.210:2181";

	/**
	 * 实例化zk客户端
	 */
	public CuratorOperator() {
		/**
		 * 同步创建zk示例，原生api是异步的
		 * 
		 * curator链接zookeeper的策略:ExponentialBackoffRetry
		 * baseSleepTimeMs：初始sleep的时间
		 * maxRetries：最大重试次数
		 * maxSleepMs：最大重试时间
		 */
//		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
		
		/**
		 * curator链接zookeeper的策略:ExponentialBackoffRetry
		 * n：重试的次数
		 * sleepMsBetweenRetries：每次重试间隔的时间
		 */
		RetryPolicy retryPolicy = new RetryNTimes(3, 5000);
		
		/**
		 * curator链接zookeeper的策略:RetryOneTime
		 * sleepMsBetweenRetry:每次重试间隔的时间
		 */
//		RetryPolicy retryPolicy = new RetryOneTime(3000);
		
		/**
		 * 永远重试，不推荐使用
		 */
//		RetryPolicy retryPolicy = new RetryForever(retryIntervalMs)
		
		/**
		 * curator链接zookeeper的策略:RetryUntilElapsed
		 * maxElapsedTimeMs:最大重试时间
		 * sleepMsBetweenRetries:每次重试间隔
		 */
//		RetryPolicy retryPolicy = new RetryUntilElapsed(2000, 3000);
		client = CuratorFrameworkFactory.builder()
				.connectString(zkServerPath)
				.sessionTimeoutMs(10000).retryPolicy(retryPolicy)
				.namespace("CuratorOperator").build();
		client.start();
	}
	
	/**
	 * 
	 * @Title: CuratorOperator.java
	 * @Package com.itzixi.curator
	 * @Description: 关闭zk客户端连接
	 * Copyright: Copyright (c) 2017
	 * Company:FURUIBOKE.SCIENCE.AND.TECHNOLOGY
	 * 
	 * @author leechenxiang
	 * @date 2017年11月17日 下午1:16:15
	 * @version V1.0
	 */
	public void closeZKClient() {
		if (client != null) {
			this.client.close();
		}
	}
	
	public static void main(String[] args) throws Exception {
		// 实例化
		CuratorOperator cto = new CuratorOperator();
		// 创建节点
		String nodePath = "/curatorNode/node3";
		byte[] data = "node33".getBytes();
//		cto.client.create().creatingParentsIfNeeded()
//			.withMode(CreateMode.PERSISTENT)
//			.withACL(Ids.OPEN_ACL_UNSAFE)
//			.forPath(nodePath, data);
		// 读取节点数据
//		Stat stat = new Stat();
//		byte[] data = cto.client.getData().storingStatIn(stat).forPath(nodePath);
//		System.out.println("读取节点" + nodePath + "的数据:" + new String(data));
//		System.out.println("数据版本号为：" + stat.getVersion());
		
		// 查询子节点
		String fatherNodePath = "/curatorNode";
//		List<String> childNodes = cto.client.getChildren()
//											.usingWatcher(new MyWatcher())	// 使用watcher，和zk原生api一样只会触发一次，不建议使用
//											.forPath(fatherNodePath);
//		for (String s : childNodes) {
//			System.out.println(s);
//		}
		
		// 更新节点数据
//		cto.client.setData().withVersion(2).forPath(fatherNodePath, data);
		
		// 判断节点是否存在,如果不存在则为空
//		Stat stat = cto.client.checkExists().forPath("/curatorNode");
//		System.out.println(stat);
		
		// 删除节点
//		cto.client.delete()
//				  .guaranteed()					// 如果删除失败，那么在后端还是继续会删除，直到成功
//				  .deletingChildrenIfNeeded()	// 如果有子节点，就删除
//				  .withVersion(2)
//				  .forPath(nodePath);
		
		// 为节点添加watcher
		// NodeCache: 监听数据节点的变更，会触发事件
//		String nodePathCache = "/lee";
//		NodeCache nodeCache = new NodeCache(cto.client, nodePathCache);
//		nodeCache.start(true);
//		nodeCache.getListenable().addListener(new NodeCacheListener() {
//			public void nodeChanged() throws Exception {
//				String data = new String(nodeCache.getCurrentData().getData());
//				System.out.println("节点路径：" + nodeCache.getCurrentData().getPath() + "数据：" + data);
//			}
//		});
		
		// 为子节点添加watcher
		// PathChildrenCache: 监听数据节点的增删改，会触发事件
		String childNodePathCache = "/lee";
		// cacheData: 设置缓存节点的数据，true表示接收到通知的时候可以获取节点的数据，false获取不到
		final PathChildrenCache childrenCache = new PathChildrenCache(cto.client, childNodePathCache, true);
		/**
		 * StartMode: 初始化方式
		 * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
		 * NORMAL：异步初始化
		 * BUILD_INITIAL_CACHE：同步初始化
		 */
		childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
		
		List<ChildData> childDataList = childrenCache.getCurrentData();
		System.out.println("当前数据节点的子节点数据列表：");
		for (ChildData cd : childDataList) {
			String childData = new String(cd.getData());
			System.out.println(childData);
		}
		
		childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				if(event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)){
					System.out.println("子节点初始化ok...");
				}else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
					System.out.println("添加子节点:" + event.getData().getPath());
					System.out.println("子节点数据:" + new String(event.getData().getData()));
				}else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
					System.out.println("删除子节点:" + event.getData().getPath());
				}else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
					System.out.println("修改子节点路径:" + event.getData().getPath());
					System.out.println("修改子节点数据:" + new String(event.getData().getData()));
				}
			}
		});
		
		Thread.sleep(60000);
	}
	
}
