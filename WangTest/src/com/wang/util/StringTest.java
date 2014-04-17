package com.wang.util;


public class StringTest {

	
	public static void main(String[] args) {
		
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
