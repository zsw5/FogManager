package com.zsw.cacheprocess;
//回收时资源排序类
public class SortKey implements Comparable<SortKey>{
	public long size;
	public String url;
	public String ipArr;
	public double filevalue;
	
	public SortKey(long s,String u,String i,double v){
		this.size=s;
		this.url=u;
		this.ipArr=i;
		this.filevalue=v;
	}
	public int compareTo(SortKey o) {
	        if(o.filevalue==this.filevalue){
	        	if(o.size-this.size>0){
	        		return -1;
	        	}else{
	        		return 1;
	        	}
	        }else{
	        	if(o.filevalue-this.filevalue>0){
	        		return -1;
	        	}else{
	        		return 1;
	        	}
	        }
	}	
}

