package com.zsw.main;

/*
 * 雾控制节点:实现与Client节点的信息通信
 */
import java.net.ServerSocket;
import java.net.Socket;

public class FogManager {

	public static void main(String[] args)throws Exception{
		 ServerSocket server = new ServerSocket(12345);
	     try {	    	
	    	 System.out.println("服务端已启动，等待用户端连接...");
	    	 while(true){
		    	 Socket socket=server.accept();
		    	 SocketThread st=new SocketThread(socket);
		    	 st.start();
		    	 
		     }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.close();
		}
	    
	}

}
