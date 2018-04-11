package com.zsw.cacheprocess;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;

import com.zsw.main.FogNodeStat;
import com.zsw.value.ValueCal;

import redis.clients.jedis.Jedis;

public class CacheProcess {
	public static Jedis jedis=new Jedis("192.168.129.98", 6379);
	public static RecycleKey[] rkArr=new RecycleKey[6];
	
	public static void main(String[] args)throws Exception{
		
		while(true){
			if(jedis.llen("waitcache")!=0){
				String cacheUrl=jedis.lpop("waitcache");
				//防止并发时重复缓存
				if(jedis.hget(cacheUrl, "cached").equals("yes")){
					continue;
				}
				System.out.println(cacheUrl+" wait cached!");
				System.out.println("start download file "+cacheUrl+" to fogmanager!");
				//首先下载到雾管理节点
				downloadToLocal(cacheUrl);
				String filename=getFilename(cacheUrl);
				long filesize=getFilesize(filename);
				System.out.println("download file to fogmanager success!");
				//选取三个合适的雾节点
				System.out.println("choose 3 fognode to store file!");
				String[] ipArr=mainProcess(filesize);					
				System.out.println("选择的三个节点为："+Arrays.toString(ipArr));
				//上传文件到三个雾节点
				storeToNode(filename,filesize,ipArr);
				//删除雾管理节点的文件
				deleteLocalFile(filename);
				//更新雾管理节点redis中信息
				updateMetaInfo(cacheUrl,filesize,ipArr);			
			}
			Thread.sleep(3000);			
		}
	}
	/** 
	 * 从网络Url中下载文件 
	 * @param urlStr 
	 * @param fileName 
	 * @param savePath 
	 * @throws IOException 
	 */  
	public static void  downloadToLocal(String urlStr) throws IOException{  
		String savePath="e:/resource";
	    URL url = new URL(urlStr);    
	    HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
	    //设置超时间为100秒  
	    conn.setConnectTimeout(3*1000);  
	    //防止屏蔽程序抓取而返回403错误  
	    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
	
	    //得到输入流  
	    InputStream inputStream = conn.getInputStream();    
	    //获取自己数组  
	    byte[] getData = readInputStream(inputStream);      
	
	    //文件保存位置  
	    File saveDir = new File(savePath);  
	    if(!saveDir.exists()){  
	        saveDir.mkdir();  
	    }  
	    String[] arr=urlStr.split("/");
	    String fileName=arr[arr.length-1];
	    File file = new File(saveDir+File.separator+fileName);      
	    FileOutputStream fos = new FileOutputStream(file); 
	    fos.write(getData);   
	    if(fos!=null){  
	        fos.close();    
	    }  
	    if(inputStream!=null){  
	        inputStream.close();  
	    }  	
	}  


	/** 
	 * 从输入流中获取字节数组 
	 * @param inputStream 
	 * @return 
	 * @throws IOException 
	 */  
	public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
	    byte[] buffer = new byte[2048];    
	    int len = 0;    
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();  
	    while((len = inputStream.read(buffer)) != -1) {    
	        bos.write(buffer, 0, len);    
	    }    
	    bos.close();    
	    return bos.toByteArray();    
	}  
	/*
	 * 获取文件名
	 */
	public static String getFilename(String url){
		String[] arr=url.split("/");
	    String fileName=arr[arr.length-1];
	    return fileName;
	}
	/*
	 * 获取文件大小
	 */
	public static long getFilesize(String filename){
		File f= new File("e:/resource/"+filename);
		return f.length();
	}
	/*
	 * 删除雾管理节点的文件
	 */
	public static void deleteLocalFile(String filename){
		String path="e:/resource/"+filename;
		File f=new File(path);
		f.delete();
		System.out.println("delete fogmanager file "+path+" success!");
	}
	/*
	 *缓存主开始函数 
	 */
	public static String[] mainProcess(long filesize){
		String[] allIP={"192.168.129.41","192.168.129.42","192.168.129.43","192.168.129.44","192.168.129.45","192.168.129.46"};
		TreeSet<StoreKey> ts=new TreeSet<>();
		chooseCacheNode(allIP, filesize, ts);
		//缓存回收过程
		/*if(ts.size()<3){
			CacheRecycle.crProcess(filesize,rkArr);
			ts.clear();
			chooseCacheNode(allIP, filesize, ts);
		}*/
		String[] resArr=new String[3];
		int count=0;
		for(StoreKey sk:ts){
			resArr[count++]=sk.ip;
			if(count==3){
				break;
			}
		}
		return resArr;
	}
	/*
	 * 选出存储空间充足的存储节点
	 */
	public static void chooseCacheNode(String[] allIP,long filesize,TreeSet<StoreKey> ts){
		int countNum=0;
		for(String singleIP:allIP){
			FogNodeStat fns=new FogNodeStat(singleIP);
			long size=fns.getStorageSize();
			double stat=fns.getFogWeight();
			long allsize=1024*1024*1024;//1G
			//存储每个节点已经使用的容量和对应的IP
			rkArr[countNum++]=new RecycleKey(allsize-size, singleIP);
			
			if(size+filesize<allsize){
				System.out.println(size+"****"+stat+"****"+singleIP);
				ts.add(new StoreKey(size, stat, singleIP));
			}		
		}
	}
	/*
	 * 从管理节点上传文件到雾节点
	 */
	public static void storeToNode(String filename,long filesize,String[] ipArr){
		for(String hostIP:ipArr){
			StoreAndDeleteProcess sdp=new StoreAndDeleteProcess(hostIP);
			sdp.upload("/var/www/html", "e:/resource/"+filename);
			System.out.println("upload file "+filename+" to "+hostIP+" success!");
		}	
	}
	/*
	 * 更新雾管理节点中redis信息
	 */
	public static void updateMetaInfo(String cacheUrl,long filesize,String[] ipArr){
		long nowtime=System.currentTimeMillis()/1000L;
		Date now=new Date();
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	    String folder=dateFormat.format(now);
	    
	    double val=ValueCal.getVal(filesize, Long.parseLong(jedis.hget(cacheUrl, "createtime")), nowtime, Integer.parseInt(jedis.hget(cacheUrl, "count")));
	    jedis.hset(cacheUrl, "usetime", nowtime+"");
		jedis.hset(cacheUrl, "filesize", filesize+"");
		jedis.hset(cacheUrl, "cached", "yes");
		jedis.hset(cacheUrl, "filevalue", val+"");
		jedis.hset(cacheUrl, "folder", folder);
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<3;i++){
			sb.append(ipArr[i]);
			if(i!=2){
				sb.append(";");
			}
		}
		jedis.hset(cacheUrl, "ipArr", sb.toString());
		System.out.println("update redis info of "+cacheUrl+" success!");
	}

}
