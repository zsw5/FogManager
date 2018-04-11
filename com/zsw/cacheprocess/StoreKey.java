package com.zsw.cacheprocess;
//用于选择存储节点排序的类
public class StoreKey implements Comparable<StoreKey> {
	long size;
	double stat;
	String ip;
	public StoreKey(long s1,double s2,String ip){
		this.size=s1;
		this.stat=s2;
		this.ip=ip;
	}

	public int compareTo(StoreKey o){
		if(this.size-o.size>0){
			return 1;
		}else if(this.size-o.size<0){
			return -1;
		}else{
			if(this.stat-o.stat>0){
				return 1;
			}else{
				return -1;
			}
		}
	}

}
