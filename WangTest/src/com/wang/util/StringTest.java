package com.wang.util;

public class StringTest {

	
	public static void main(String[] args) {
		test1();
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

}
