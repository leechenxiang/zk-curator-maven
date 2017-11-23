package com.itzixi.service;

public interface OrderService {

	public boolean createOrder(int buyCounts);

	public boolean createOrderSLockTest(int operator, int buyCounts);
}

