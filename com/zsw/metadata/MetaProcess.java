package com.zsw.metadata;
import java.util.HashMap;
import java.util.Map;

import com.zsw.main.FogNodeStat;
import com.zsw.value.ValueCal;

/*
 * 负责操作reids中的元数据
 */
import redis.clients.jedis.Jedis;

/*
 *  String url;//url标识
 *	long createtime;//创建时间
 *	long usetime;//最近使用时间
 *	int count;//访问次数
 *	String[] ipArr;//三副本的IP列表
 *	long filesize;//文件大小
 *	String filename;//实际文件名称
 *	double filevalue;//文件的效用价值
 *	String folder;//存储目录
 *	String cached;//是否已缓存
 */
public class MetaProcess {
	private static Jedis jedis=null;
	private static String redisHost="192.168.129.98";
	private static int redisPort=6379;
	
	public MetaProcess(){
		jedis=new Jedis(redisHost,redisPort);
	}
	/*
	 * 得到FogManager返回给FogTest的字符串数据
	 * reciver:FogTest传给FogManager的url字符串集合,以;分割
	 * return:FogMangaer返回给FogTest的字符串集合,以;分割
	 */
	public String getResult(String reciver){
		long point3=System.currentTimeMillis();
		String[] strArr=reciver.split(";");
		StringBuffer sb=new StringBuffer();
		
		for(String eachurl:strArr){		
			System.out.println("请求资源的url="+eachurl);
			if(jedis.exists(eachurl)){//redis集 群中有此url的记录
				//获取访问次数
				int count=Integer.parseInt(jedis.hget(eachurl, "count"));
				if(jedis.hget(eachurl, "cached").equals("no")){//还未到达缓存标准，未缓存
					//达到缓存标准,后续的处理步骤
					if(count+1>=getThreshold()){
						jedis.hset(eachurl, "count", count+1+"");
						cacheProcess(eachurl);
					}else{
						//仍未达到缓存标准
						jedis.hset(eachurl, "count", count+1+"");
					}					
					sb.append(eachurl);
					System.out.println("download from cloud...");
				}else{//已经缓存
					updateMetaData(eachurl, count);
					System.out.println("range1=="+(System.currentTimeMillis()-point3));
					String resIP=nodeChoose(eachurl);
					System.out.println("range2=="+(System.currentTimeMillis()-point3));
					String resUrl="http://"+resIP+"/"+jedis.hget(eachurl, "filename");
					sb.append(resUrl);
				}			
			}else{//redis集群中没有此url的记录
				long createtime=System.currentTimeMillis()/1000L;
				Map<String, String> map=new HashMap<>();
				map.put("createtime", createtime+"");
				map.put("url", eachurl);
				map.put("filename", getFilename(eachurl));
				map.put("filevalue", "0");
				map.put("count", "1");
				map.put("cached", "no");
				jedis.hmset(eachurl, map);
				sb.append(eachurl);
				System.out.println("download from cloud...");
			}
			sb.append(";");
		}
		System.out.println("range3==="+(System.currentTimeMillis()-point3));
		return sb.toString().substring(0, sb.length()-1);
	}
	/*
	 * 获取url中实际的文件名称
	 */
	public String getFilename(String url){
		String[] arr=url.split("/");
	    String fileName=arr[arr.length-1];
	    return fileName;
	}
	/*
	 * 三副本机制中的节点选择
	 */
	public String nodeChoose(String eachurl){
		String[] ipArr=jedis.hget(eachurl, "ipArr").split(";");
		/*String resIP="";
		double minVal=Double.MAX_VALUE;
		for (String singleIP : ipArr) {
			FogNodeStat fns=new FogNodeStat(singleIP);
			double val=fns.getFogWeight();
			if(val<minVal){
				minVal=val;
				resIP=singleIP;
			}
		}
		*/
		int index=(int)(3*Math.random());
		String resIP=ipArr[index];
		System.out.println("download "+eachurl+" from "+resIP);
		System.out.println();
		return resIP;
	}
	/*
	 * 获取阈值
	 */
	public int getThreshold(){
		return Integer.parseInt(jedis.get("threshold"));
	}
	/*
	 * 达到缓存标准后的缓存步骤
	 */
	public void cacheProcess(String url){
		//把需要缓存的url存入队列中，等待缓存

		jedis.lpush("waitcache", url);
	}
	/*
	 * 更新元数据
	 */
	public void updateMetaData(String url,int count){
		long nowtime=System.currentTimeMillis()/1000L;
		
		jedis.hset(url, "count", count+1+"");
		jedis.hset(url, "usetime", nowtime+"");
		
	}

}
