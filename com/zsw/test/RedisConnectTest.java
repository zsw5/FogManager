package com.zsw.test;

import redis.clients.jedis.Jedis;

public class RedisConnectTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Jedis jedis=new Jedis("192.168.129.98", 6379);
		System.out.println("连接成功");
		if(jedis.exists("http://test.com/a.jpg")){
			System.out.println(jedis.get("http://test.com/a.jpg"));
		}
		jedis.close();

	}

}
