package com.zsw.test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTest {

	public static void main(String[] args) {
        Date now=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String tablename=dateFormat.format(now);
        System.out.println(tablename);

	}

}
