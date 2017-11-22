package com.itzixi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itzixi.service.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderController extends BaseController {
	
	final static Logger log = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private OrderService orderService;
	
	@RequestMapping("/display1")
	@ResponseBody
	public String display1(int buyCounts){
		
		boolean falg = orderService.createOrder(buyCounts);
		return falg ? "buy success..." : "buy failed...";
	}
	
	@RequestMapping("/display2")
	@ResponseBody
	public String display2(int buyCounts){
		
		boolean falg = orderService.createOrder(buyCounts);
		return falg ? "buy success..." : "buy failed...";
	}

}
