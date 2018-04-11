package com.zsw.cacheprocess;
//表示剩余空间与对应ip的类
public class RecycleKey {
	public long freeSize;//表示剩余空间
	public String ip;
	public RecycleKey(long freeSize,String ip){
		this.freeSize=freeSize;
		this.ip=ip;
	}

}
