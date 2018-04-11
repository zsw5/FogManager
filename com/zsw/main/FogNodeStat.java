package com.zsw.main;
/*
 * 负责获取雾节点的负载信息(cpu\memory\IO\带宽)
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class FogNodeStat {
	private String charset = "UTF-8"; // 设置编码格式
    private String user="root"; // 用户名
    private String passwd="wmct1234"; // 登录密码
    private String host; // 主机IP
    private JSch jsch;
    private Session session;
    private ChannelExec channel=null;
    private InputStream in=null;
    private BufferedReader reader=null;
    
    /**
     * 
     * @param user用户名
     * @param passwd密码
     * @param host主机IP
     */
    public FogNodeStat(String host) {
        this.host = host;
    }

    /**
     * 连接到指定的IP
     * 
     * @throws JSchException
     */
    public void connect(){
    	try{
    		jsch = new JSch();
            session = jsch.getSession(user, host, 22);
            session.setPassword(passwd);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
    	}catch(JSchException e){
    		e.printStackTrace();
    	}
    }
    
   
    /*
     * 获取CPU使用率
     */
    public float getCPUStat()throws Exception{
    	String command="cd /opt/stat/cpustat;bash cpuStat.sh";
    	channel = (ChannelExec)session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        channel.connect();
        in = channel.getInputStream();
        reader = new BufferedReader(new InputStreamReader(in,Charset.forName(charset)));
        float cpuStat=Float.parseFloat(reader.readLine());
        channel.disconnect();
        return cpuStat;
    }
    /*
     * 获取内存使用率
     */
    public float getMemStat()throws Exception{
    	String command="cd /opt/stat/memstat;bash memStat.sh";
    	channel = (ChannelExec)session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        channel.connect();
        in = channel.getInputStream();
        reader = new BufferedReader(new InputStreamReader(in,Charset.forName(charset)));
        float memStat=Float.parseFloat(reader.readLine());
        channel.disconnect();
        return memStat;
    }
    /*
     * 获取IO使用率
     */
    public float getIOStat()throws Exception{
    	String command="cd /opt/stat/iostat;bash ioStat.sh";
    	channel = (ChannelExec)session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        channel.connect();
        in = channel.getInputStream();
        reader = new BufferedReader(new InputStreamReader(in,Charset.forName(charset)));
        float ioStat=Float.parseFloat(reader.readLine());
        channel.disconnect();
        return ioStat;
   }
    /*
     * 获取带宽占用率
     */
    public float getNetStat()throws Exception{
    	String command="cd /opt/stat/netstat;bash netStat.sh";
    	channel = (ChannelExec)session.openChannel("exec");
        channel.setCommand(command);
        channel.setInputStream(null);
        channel.setErrStream(System.err);

        channel.connect();
        in = channel.getInputStream();
        reader = new BufferedReader(new InputStreamReader(in,Charset.forName(charset)));
        float netStat=Float.parseFloat(reader.readLine());
        channel.disconnect();
        return netStat;
    }
    /*
     * 获得雾节点存储目录下总的缓存大小
     */
    public long getStorageSize(){
    	this.connect();
    	long storageSize=0; 
    	try{
    		String command="du -sb /var/www/html/ | awk '{print $1}'";
        	channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            channel.connect();
            in = channel.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in,Charset.forName(charset)));
            storageSize=Long.parseLong(reader.readLine());    
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally {
    		channel.disconnect();
            session.disconnect();
		}
        return storageSize;
    }
    /*
     * 获取由CPU、MEM、IO、带宽组成的加权值
     */
    public double getFogWeight(){
    	this.connect();
    	double weight=0f; 
    	try{
    		String command="cd /opt/stat;bash getStat.sh";
        	channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            channel.connect();
            in = channel.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in,Charset.forName(charset)));
            weight=Double.parseDouble(reader.readLine());    
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally {
    		channel.disconnect();
            session.disconnect();
		}
    	System.out.println(host+"......"+weight);
        return weight;
    }
    
    
}
