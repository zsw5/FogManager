package com.zsw.value;

//计算缓存资源的效用值

public class ValueCal {
	
	public static double getVal(long filesize,long createtime,long usetime,int count){
		
		//Optimal Cache Value Replacement Algorithm(OCV算法)
		//p=count-(nowtime-createtime);
		//s=filesize;
		//k=(expiretime-nowtime)/(nowtime-creatimetime);
		//value=p*s*k
		//return value;
		
		double value=Math.random();
		
		return value; 
		
		
	}
}