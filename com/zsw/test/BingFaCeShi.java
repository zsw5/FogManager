package com.zsw.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;


public class BingFaCeShi {
	private static final int NUM=50;
	 final CyclicBarrier barrier = new CyclicBarrier(NUM,  
	            new Runnable()  
	            {  
	                @Override  
	                public void run()  
	                {  
	                    System.out.println("全部建好了，开始下载吧");  
	                }  
	            }); 
	 
	 public void process() throws Exception 
	 {  
	     System.out.println(Thread.currentThread().getName() + "已建好");  
	     barrier.await();  
	     System.out.println(Thread.currentThread().getName() + "开始下载");
	     downLoadFromUrl(url,"e:/resource");  
	     latch.countDown();
	 } 
	 
	private static String url="http://192.168.129.43/eEOY-fwnpcns5695175.jpg";
	private static CountDownLatch latch = new CountDownLatch(NUM);
    public static void end(long start){
		long end=System.currentTimeMillis();
		System.out.println("end="+end);
		System.out.println(end-start);
    }
    public static void main(String[] args)throws Exception{
    	long start=System.currentTimeMillis();
    	System.out.println("start="+start);
    	
    	final BingFaCeShi instance = new BingFaCeShi();  
    	
        for (int i = 0; i < NUM; i++)  
        {  
            new Thread()  
            {  
                public void run()  
                {  
                    try  
                    {  
                        instance.process();  
                    } catch (Exception e)  
                    {  
                        e.printStackTrace();  
                    }  
  
                };  
  
            }.start();  
        }
        latch.await();
        end(start);
    }
            	   
    public static void  downLoadFromUrl(String urlStr,String savePath) throws IOException{  
	    URL url = new URL(urlStr);    
	    HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
	    //设置超时间为10秒  
	    conn.setConnectTimeout(10*1000);  
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
	    File file = new File(saveDir+File.separator+Thread.currentThread().getName()+"-"+fileName);      
	    FileOutputStream fos = new FileOutputStream(file); 
	    fos.write(getData);   
	    if(fos!=null){  
	        fos.close();    
	    }  
	    if(inputStream!=null){  
	        inputStream.close();  
	    }  	
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
	    while((len = inputStream.read(buffer)) != -1) {    
	        bos.write(buffer, 0, len);  
	    }     
	    bos.close();    
	    
	    return bos.toByteArray();    
	}    
}
