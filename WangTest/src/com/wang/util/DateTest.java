package com.wang.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTest {

	
	public static void main(String[] args) throws Exception {
		
		Date date = new Date(1396796258082l);
		System.out.println(date);
		
		
		Date date2 = new Date(1396796258251l);
		System.out.println(date2);
		
//		test1();                             1385308793000
        //System.out.println("out:" + new Date(1358264234000l) + "   "  + "out2:" + new Date(1385308793000l));
		                                     
		//1353474554
//		System.out.println((new Date()).getTime());
		
//		System.out.println("t2" + new Date(1384848352000l));
		
//		timeStampDateTest();
//		test2();
//		complexDateFormat( );
	}
	
	public static void test1() {
		
		Date date2 = new Date(1382410374000l);
		System.out.println(date2);
		
		Date date = new Date(1383101591000l);
		System.out.println(date);
		

		
		long time = System.currentTimeMillis();
		long nanoTime = System.nanoTime();
		long time2 = nanoTime/1000;
		
		Date date3 = new Date(nanoTime);
		System.out.println(" date3:" + date3);
	}
	
	public static void test2() throws Exception{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		
		Date date = dateFormat.parse("2013-11-30 00:20");
		
		Date date2 = dateFormat.parse("2013-11-30 23:40");
		
		System.out.println(date.getTime() + " date2:" + date2.getTime() );
	}
	
	public static void timeStampDateTest( ) {
		Date date = new Date();
		long time = date.getTime();
		
		Timestamp timeStamp = new java.sql.Timestamp(time);
		
		System.out.println(" time:" + time + " " + timeStamp.getTime());
	}
	
	
//	"Mon Jul 16 18:09:20 +0800 2012"
	public static void complexDateFormat( ) throws Exception {
		Date date = new Date(1395740958686l);
		System.out.println(date);
		
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy", Locale.US);
		
		String str = "Mon Jul 16 18:09:20 +0800 2012";
		Date date2 = dateFormat.parse( str );
		
		
		DateFormat dateFormat2 = new SimpleDateFormat("EEE MMM d HH:mm:ss Z yyyy");
		
		System.out.println( dateFormat2.format( date2 ) );
//		System.out.println( dateFormat.format(date) );
	}
}
