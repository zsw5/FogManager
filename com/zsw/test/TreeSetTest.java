package com.zsw.test;

import java.util.Iterator;
import java.util.TreeSet;

public class TreeSetTest {
	public static void main(String[] args){
		TreeSet<Integer> ts=new TreeSet<>();
		ts.add(4);
		ts.add(6);
		ts.add(1);
		ts.add(9);
		ts.add(-1);
		Iterator<Integer> it=ts.iterator();
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}

}
