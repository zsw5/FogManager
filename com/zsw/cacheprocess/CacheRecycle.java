package com.zsw.cacheprocess;

import java.util.Set;
import java.util.TreeSet;

import redis.clients.jedis.Jedis;

public class CacheRecycle {
	public static Jedis jedis; 
	public static Set<SortKey> sortTree=new TreeSet<>();
	public CacheRecycle(){
		
	}
	//初始化排序二叉树
	public static void init(){
		jedis=new Jedis("192.168.129.98", 6379);
		Set<String> s=jedis.keys("http*");
		sortTree.clear();
		for(String url:s){
			sortTree.add(new SortKey(Long.parseLong(jedis.hget(url, "filesize")), url, jedis.hget(url, "ipArr"), Double.parseDouble(jedis.hget(url, "filevalue"))));
		}
		/*for(SortKey sk:sortTree){
		System.out.println(sk.size+"***"+sk.url+"***"+sk.ipArr);
		}*/
	}
	/*
	 * 缓存回收函数
	 */
	public static void crProcess(long filesize,RecycleKey[] rkArr){
		System.out.println("cache process start!");
		init();
		for(SortKey sk:sortTree){
			System.out.println(sk.size+"**"+sk.url+"**"+sk.ipArr+"**"+sk.filevalue+" will be recycled!");
			String[] ipArr=sk.ipArr.split(";");
			for(String singleIp:ipArr){
				StoreAndDeleteProcess sdp=new StoreAndDeleteProcess(singleIp);
				sdp.delete("/var/www/html", getFilename(sk.url));
			}
			//更新剩余容量
			updateSize(rkArr,sk.size,ipArr);
			jedis.del(sk.url);
			//判断是否回收结束
			int count=0;
			for(int i=0;i<6;i++){
				if(rkArr[i].freeSize>=filesize){
					count++;
				}
			}
			if(count>=3){
				break;
			}
		}		
	}
	
	/*
	 * 更新存储空间大小
	 */
	public static void updateSize(RecycleKey[] rkArr,long size,String[] ipArr){
		for(String singleIP:ipArr){
			if(singleIP.equals("192.168.129.41")){
				rkArr[0].freeSize+=size;
			}
			if(singleIP.equals("192.168.129.42")){
				rkArr[1].freeSize+=size;
			}
			if(singleIP.equals("192.168.129.43")){
				rkArr[2].freeSize+=size;
			}
			if(singleIP.equals("192.168.129.44")){
				rkArr[3].freeSize+=size;
			}
			if(singleIP.equals("192.168.129.45")){
				rkArr[4].freeSize+=size;
			}
			if(singleIP.equals("192.168.129.46")){
				rkArr[5].freeSize+=size;
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
		
	}
}
