package com.zsw.cacheprocess;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class ExpiresControl {
	//获得过期时间unix时间戳
	public static long getExpiresTime(String url){
		URL destURL = null;  
        HttpURLConnection httpUrlCon= null;  
        long time=0;
        long nowtime=System.currentTimeMillis()/1000;
        try {  
            destURL = new URL(url);  
            httpUrlCon = (HttpURLConnection)destURL.openConnection();              
            httpUrlCon.connect();
            String timeStr="";          
            if((timeStr=httpUrlCon.getHeaderField("Cache-Control"))!=null){
            	String[] arr=timeStr.split("=");
            	time=nowtime+Long.parseLong(arr[1]);
            }else if((timeStr=httpUrlCon.getHeaderField("Expires"))!=null){
            	time=shijian(timeStr);
            }else{
            	long Date=shijian(httpUrlCon.getHeaderField("Date"));
                long Last_Modified=shijian(httpUrlCon.getHeaderField("Last-Modified"));
                time=nowtime+(long)((Date-Last_Modified)*0.1);
            }

        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return time;
		
	}
	//资源一致性校验
	public static boolean isConsistent(String url,String etags,String if_modified_since){
		boolean flag=false;		
        try { 
        	URL destURL = new URL(url);  
            HttpURLConnection httpUrlCon= (HttpURLConnection)destURL.openConnection();
        	if(if_modified_since!=null){
        		httpUrlCon.setRequestProperty("If-Modified-Since", if_modified_since);
        		httpUrlCon.connect();
        		if(httpUrlCon.getResponseCode()==304){
        			flag=true;
        		}else{
        			flag=false;
        		}
    		}else if(etags!=null){
    			httpUrlCon.setRequestProperty("If-None-Match", etags);
    			httpUrlCon.connect();
    			if(httpUrlCon.getResponseCode()==304){
    				flag=true;
    			}else{
    				flag=false;
    			}
    		}
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
		
		return flag;
	}
	//时间格式转换
	public static long shijian(String s){
		s=s.substring(0, s.length()-4);
		long epoch=0;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", java.util.Locale.UK);		
			epoch = sdf.parse(s).getTime()/1000;			
		}catch(Exception e){
			e.printStackTrace();
		}
		return epoch;
	}
	public static void main(String[] args)throws Exception{
            
	}


}
