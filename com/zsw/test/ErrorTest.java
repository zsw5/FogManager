package com.zsw.test;

import redis.clients.jedis.Jedis;

public class ErrorTest {

	public static void main(String[] args) {
		Jedis jedis=new Jedis("192.168.129.98", 6379);
		String cacheUrl="http://sw.bos.baidu.com/sw-search-sp/software/e80aba170ee7c/ChromeStandalone_62.0.3202.94_Setup.exe";
		Long ll=Long.parseLong(jedis.hget(cacheUrl, "createtime"));
		long lll=Integer.parseInt(jedis.hget(cacheUrl, "count"));
		System.out.println(ll+"..."+lll);
		jedis.close();
	    //double val=ValueCal.getVal(filesize, Long.parseLong(jedis.hget(cacheUrl, "createtime")), nowtime, Integer.parseInt(jedis.hget(cacheUrl, "count")));
	}

}
