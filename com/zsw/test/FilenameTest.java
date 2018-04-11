package com.zsw.test;

public class FilenameTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s="http://www.baidu.com/cc/a.jpg";
		String[] sArr=s.split("/");
		System.out.println(sArr[sArr.length-1]);

	}

}
