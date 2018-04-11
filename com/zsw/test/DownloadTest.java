package com.zsw.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTest{
	/** 
	 * 从网络Url中下载文件 
	 * @param urlStr 
	 * @param fileName 
	 * @param savePath 
	 * @throws IOException 
	 */  
	public static void  downLoadFromUrl(String urlStr,String savePath) throws IOException{  
	    URL url = new URL(urlStr);    
	    HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
	    //设置超时间为3秒  
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
	    System.out.println("");
	    System.out.println("info:"+url+" download success");   	
	}  


	/** 
	 * 从输入流中获取字节数组 
	 * @param inputStream 
	 * @return 
	 * @throws IOException 
	 */  
	public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
	    byte[] buffer = new byte[4096];    
	    int len = 0;   
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();  
	   // int i=0;
	    while((len = inputStream.read(buffer)) != -1) {    
	        bos.write(buffer, 0, len);  
	       //System.out.println(i++);
	    }     
	    bos.close();    
	    
	    return bos.toByteArray();    
	}    

	public static void main(String[] args) {  
		long start=System.currentTimeMillis();
		//http://sw.bos.baidu.com/sw-search-sp/software/e612acbcdf789/IQIYIsetup_1001_6.2.57.5300.exe
		System.out.println("start="+start);
	    try{  
	        downLoadFromUrl("https://i.sso.sina.com.cn/images/login/weibo_how_ot.png","e:/resource");  
	    }catch (Exception e) {  
	        e.printStackTrace(); 
	    }  
	    long end=System.currentTimeMillis();
	    System.out.println("end="+end);
	    System.out.println(end-start);
	}  
}
