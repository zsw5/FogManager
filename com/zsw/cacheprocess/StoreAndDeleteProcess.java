package com.zsw.cacheprocess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class StoreAndDeleteProcess {	
    private String user="root"; // 用户名
    private String passwd="wmct1234"; // 登录密码
    private int port=22;
    private JSch jsch;
    private Session sshSession;
	private ChannelSftp sftp;

    public StoreAndDeleteProcess(String host) {    
    		try {  
	    		jsch = new JSch();  
	    		jsch.getSession(this.user, host, this.port);  
	    		sshSession = jsch.getSession(this.user, host, this.port);  
	    		sshSession.setPassword(this.passwd);  
	    		Properties sshConfig = new Properties();  
	    		sshConfig.put("StrictHostKeyChecking", "no");  
	    		sshSession.setConfig(sshConfig);  
	    		sshSession.connect();  
	    		Channel channel = sshSession.openChannel("sftp");  
	    		channel.connect();  
	    		sftp = (ChannelSftp) channel;  
	    		System.out.println("Connected to " + host + "......");  
    		} catch (Exception e) {  
    		  
    		}  
    } 
    		  
    /** 
    * 上传文件 
    * @param directory 上传的目录 
    * @param uploadFile 要上传的文件 
    * @param sftp 
    */  
    public void upload(String directory, String uploadFile) {  
    	FileInputStream fis=null;
	    try {  
	    	this.sftp.cd(directory);  
	    	File file=new File(uploadFile);  
	    	fis=new FileInputStream(file);
	    	this.sftp.put(fis, file.getName());  
	    } catch (Exception e) {  
	    	e.printStackTrace();  
	    } finally{
	    	try {
	    		fis.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}	    	
	    	this.sftp.disconnect();
	    	this.sshSession.disconnect();
	    } 
    }  
    		  
    /** 
    * 下载文件 
    * @param directory 下载目录 
    * @param downloadFile 下载的文件 
    * @param saveFile 存在本地的路径 
    * @param sftp 
    */  
    public void download(String directory, String downloadFile,String saveFile) {  
    	FileOutputStream fos=null;
	    try {  
	    	this.sftp.cd(directory);  
	    	File file=new File(saveFile);  
	    	fos=new FileOutputStream(file);
	    	this.sftp.get(downloadFile, fos);  
	    } catch (Exception e) {  
	    	e.printStackTrace();  
	    } finally{
	    	try {
	    		fos.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
	    	this.sftp.disconnect();
	    	this.sshSession.disconnect();
	    } 
    }  
    		  
    /** 
    * 删除文件 
    * @param directory 要删除文件所在目录 
    * @param deleteFile 要删除的文件 
    * @param sftp 
    */  
    public void delete(String directory, String deleteFile) {  
    	try {  
    		this.sftp.cd(directory);  
    		this.sftp.rm(deleteFile);  
    	} catch (Exception e) {  
    		e.printStackTrace();  
    	} finally{
	    	this.sftp.disconnect();
	    	this.sshSession.disconnect();
	    }  
    } 
    public static void main(String[] args){
    	StoreAndDeleteProcess sdp=new StoreAndDeleteProcess("192.168.129.41");
    	sdp.upload("/var/www/html", "e:/resource/WPS2016Pro_normal.exe");    	  	
    }

}
