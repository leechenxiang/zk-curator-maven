package com.itzixi.curator;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

public class MyWatcher implements CuratorWatcher {

	public void process(WatchedEvent event) throws Exception {
		System.out.println("触发watcher时间通知，地址为：" + event.getPath());
	}

}
