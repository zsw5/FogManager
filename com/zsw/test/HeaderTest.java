package com.zsw.test;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HeaderTest {  
    public static void main(String[] args) {  
        String URI= "https://csdnimg.cn/public/common/toolbar/js/content_toolbar.js";  
  
        try {  
           URL destURL = new URL(URI);            
           HttpURLConnection httpUrlCon = (HttpURLConnection)destURL.openConnection();  
            
           httpUrlCon.setRequestProperty("If-None-Match", "F2AFAD18E315D1BBF246E3146891C836");
            
           httpUrlCon.connect();  
           
           int responseCode=httpUrlCon.getResponseCode();
           String Etag=httpUrlCon.getHeaderField("Etag");
            
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
   
}  


