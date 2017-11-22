package com.itzixi.service.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itzixi.curator.utils.ZKLockWebUtil;
import com.itzixi.mapper.ItemsMapper;
import com.itzixi.mapper.OrdersMapper;
import com.itzixi.pojo.Items;
import com.itzixi.pojo.Orders;
import com.itzixi.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	
	final static Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	@Autowired
	private ZKLockWebUtil zkLockWebUtil;
	
	@Autowired
	private ItemsMapper itemsMapper;
	
	@Autowired
	private OrdersMapper ordersMapper;

	@Override
	public boolean createOrder(int buyCounts) {
		
		// 加入分布式锁 - 排它锁
//		ZKLockUtil.init("192.168.1.210:2181");
//		ZKLockUtil.getXLock();
		zkLockWebUtil.getXLock();
		
		// 查询商品库存，库存够则可以购买，库存不够则不能购买
		Items item = itemsMapper.selectByPrimaryKey("1");
		if (item.getCounts() < buyCounts) {
			log.info("{}的库存剩余{}件，用户需求量{}件，库存不足，订单创建失败...", item.getName(), item.getCounts(), buyCounts);
			// 释放锁
//			ZKLockUtil.releaseXLock();
			zkLockWebUtil.releaseXLock();
			return false;
		}
		
		// 模拟并发 
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// 创建订单
		String oid = UUID.randomUUID().toString();
		Orders o = new Orders();
		o.setId(oid);
		o.setOrderNum(oid);
		o.setItemId(item.getId());
		ordersMapper.insert(o);
		
		// 减少库存
		Items reduceItem = new Items();
		reduceItem.setId("1");
		reduceItem.setBuyCounts(buyCounts);
		itemsMapper.reduceCounts(reduceItem);
				
		log.info("订单创建成功");
		
		// 释放锁
//		ZKLockUtil.releaseXLock();
		zkLockWebUtil.releaseXLock();
		
		return true;
	}


}
