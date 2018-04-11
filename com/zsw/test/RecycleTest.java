package com.zsw.test;

import java.util.Set;
import java.util.TreeSet;

import com.zsw.cacheprocess.SortKey;
import com.zsw.cacheprocess.StoreAndDeleteProcess;

import redis.clients.jedis.Jedis;

public class RecycleTest {

	public static Jedis jedis; 
	public static Set<SortKey> sortTree=new TreeSet<>();
	//初始化排序二叉树
	public static void init(){
		jedis=new Jedis("192.168.129.98", 6379);
		Set<String> s=jedis.keys("http*");
		sortTree.clear();
		for(String url:s){
			sortTree.add(new SortKey(Long.parseLong(jedis.hget(url, "filesize")), url, jedis.hget(url, "ipArr"), Double.parseDouble(jedis.hget(url, "filevalue"))));
		}
		for(SortKey sk:sortTree){
			System.out.println(sk.size+"***"+sk.url+"***"+sk.ipArr+"***"+sk.filevalue);
		}
	}
	/*
	 * 缓存回收函数
	 */
	public static void crProcess(){
		System.out.println("cache process start!");
		init();
		int count=0;
		for(SortKey sk:sortTree){
			System.out.println(sk.size+"**"+sk.url+"**"+sk.ipArr+"**"+sk.filevalue+" will be recycled!");
			String[] ipArr=sk.ipArr.split(";");
			for(String singleIp:ipArr){
				StoreAndDeleteProcess sdp=new StoreAndDeleteProcess(singleIp);
				sdp.delete("/var/www/html", getFilename(sk.url));
			}
			
			jedis.del(sk.url);
			count++;
			if(count==1){
				break;
			}
			
		}		
	}
	
	
	/*
	 * 获取文件名
	 */
	public static String getFilename(String url){
		String[] arr=url.split("/");
	    String fileName=arr[arr.length-1];
	    return fileName;
	}
	public static void main(String[] args){
		crProcess();
	}

}
