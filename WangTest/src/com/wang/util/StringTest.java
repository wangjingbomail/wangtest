package com.wang.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class StringTest {
	
    private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    private static DateFormat dateHourFormat = new SimpleDateFormat("yyyyMMddHH");
	   /**
     * 获得下周星期天的日期字符串。举例:20140706
     *
     * @return
     */
    public static String getSundayStr() {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.WEEK_OF_YEAR, 2);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        return dateFormat.format(calendar.getTime());
    }

    public static String getThisSundayStr() {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        return dateFormat.format(calendar.getTime());
    }
	
	public static void main(String[] args) {
		
		System.out.println(getThisSundayStr());
		System.out.println(getSundayStr());
		
		System.out.println(new Date(1404403199000l));
		System.out.println(new Date(1402502400000l));
		
		System.out.println(Integer.MAX_VALUE + -1);
		long a = 345;
		
		System.out.println(a*1.0/10);
		String str = "1506.test";
		String[] array = str.split("\\.");
		
		System.out.println(array);
//		String a = " order_id in ('123')";
//		System.out.println(a.replace('\'', ' '));
//		test1();
//		test3();
		
//		String a = null;
//		String b = String.valueOf(a);
//		System.out.println(b);
//		String b = a + "1001";
//		System.out.println(b);
//		String notify_time = "|41|45";
//	    String[] timeArray = notify_time.split("\\|");
//	    
//	    
//	    System.out.println("first:"+timeArray[0]+ " " + timeArray[1]);
//	    
//	    System.out.println(" " + Long.parseLong(timeArray[0]));
	}
	
	public static void test1 () {
		for(int i=0 ; i <10; i++) {
			long begin = System.nanoTime();
			StringBuilder builder = new StringBuilder();
			
			builder.append("12345");
			builder.append("3");
			builder.append("6");
			
			String str = builder.toString();
			long end = System.nanoTime();
			
			
			long begin1 = System.nanoTime();
			String str2 = "12345" + "3" + "6";
			long end1 = System.nanoTime();
			
			System.out.println((end-begin) + " " + (end1-begin1));
			
		}
	}
	
	public static void test3() {
		for(int i=0; i<100; i++) {
		    if (i<10) {
		    	System.out.println("select * from payacc_order_0" + i + " where userid_to='2008088173';");
		    }else{
		    	System.out.println("select * from payacc_order_" + i + " where userid_to='2008088173';");
		    } 
		    	
			
		}
	}
 
}
