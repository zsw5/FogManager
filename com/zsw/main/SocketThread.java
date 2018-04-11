package com.zsw.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.zsw.metadata.MetaProcess;

/**
 * Socket多线程处理类 用来处理服务端接收到的客户端请求（处理Socket对象）
 */
public class SocketThread extends Thread {
    private Socket socket;

    public SocketThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
        	System.out.println("point1==="+System.currentTimeMillis());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String temp = null;
            String info = "";
            while ((temp = reader.readLine()) != null) {
                info += temp;
            }
            System.out.println("point2==="+System.currentTimeMillis());
            MetaProcess mp=new MetaProcess();
            String res=mp.getResult(info);
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println(res);
            writer.flush();
            socket.shutdownOutput();

            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}