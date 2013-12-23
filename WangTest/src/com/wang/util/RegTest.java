package com.wang.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {

	public static void main(String[] args) {
		test3();
	}
	
	public static void test1() {
		
		Pattern pattern = Pattern.compile(",\\d{2,}?ms.*");
//		                  pattern.
		Matcher matcher = pattern.matcher(",123ms");
		System.out.println(matcher.matches());
		System.out.println(matcher.group());
		
		                                   
		Pattern pattern1 = Pattern.compile(",2[2-9]\\dms");
		Matcher matcher1 = pattern1.matcher(",233ms");
		System.out.println(matcher1.matches());
		
		//含有20ms或者以上的内容
		Pattern pattern2 = Pattern.compile(".*,[2-9]\\dms.*|.*,\\d{3,}?ms.*" );
		Matcher matcher3 = pattern2.matcher(",13ms");
		System.out.println(matcher3.matches());
		
		Matcher matcher2 = pattern2.matcher(",23ms");
		System.out.println(matcher2.matches());
		
		Matcher matcher4 = pattern2.matcher(",33ms");
		System.out.println(matcher4.matches());
		
		Matcher matcher5 = pattern2.matcher(",99ms");
		System.out.println(matcher5.matches());
		
		Matcher matcher6 = pattern2.matcher(",100ms");
		System.out.println(matcher6.matches());

		
	}
	
	public static void test2() {
		Pattern pattern = Pattern.compile("(,\\d{2,}?ms)");

        Matcher matcher = pattern.matcher("dff,123mst");
        
        while(matcher.find()) {
        	System.out.println(matcher.group() + " " + matcher.start() + " " + matcher.end() );
        }
//        System.out.println(matcher.matches());
//        System.out.println(matcher.group());
		
		
//		 Pattern pattern = Pattern.compile("ab", Pattern.CASE_INSENSITIVE);
//	        Matcher matcher = pattern.matcher("ABcabdAb");
//	        // using Matcher find(), group(), start() and end() methods
//	        while (matcher.find()) {
//	            System.out.println("Found the text \"" + matcher.group()
//	                    + "\" starting at " + matcher.start()
//	                    + " index and ending at index " + matcher.end());
//	        }
	}
	
	
	public static void test3() {
		String log1 = "api.weibo.com 10.73.32.206 27 - [21/Dec/2013:21:00:13 +0800] \"GET /vpayBank/Pay?sp=5001&gateBill=13124440191100&sign=78d9493234735da2ce1e2c6ea54b379b HTTP/1.1\" 200 641 - - -";
		String log2 = "i.paycore.weibo.com 10.73.27.23 3us - [22/Dec/2013:00:00:03 +0800] \"GET /remoting/balance_by_user.htm?userid=2006418310&interfaceversion=v1.0.0&applytime=1387641603&platform=4&sign=b010e02874672e782121f91460042cf3 HTTP/1.1\" 200 121";
		String log3 = "i.paycore.weibo.com 10.73.27.23 55us - [22/Dec/2013:00:00:03 +0800] \"POST /remoting/consume_finish.htm HTTP/1.1\" 200 181" ;
		String log4 = "\"POST /pay/order_status.json HTTP/1.0\" 200 115 - -";
		String[] logArray = new String[]{log1, log2, log3, log4};
		
//		String regex = "(\"GET|\"POST)\\s\\S*\\sHTTP";
//		String regex = "(\"GET|\"POST)\\s[^?]*\\s";
//		String regex = "(\"GET|\"POST)\\s([^?]*\\s|\\S*\\sHTTP)" ;
		
		String regex = "\".*\"";
		
		
		Pattern pattern = Pattern.compile(regex);
		
		
		for(String log:logArray) {
		    Matcher matcher = pattern.matcher(log);
		    
		    String httpStr  ="";
		    if (matcher.find()) {
		    	httpStr = matcher.group();
	           	
	        }
		    
		    if ("".equals(httpStr)==false ) {
		        String[] array = httpStr.split(" ");
		        
		        int index= array[1].indexOf("?");
		        
		        if (index==-1) {
		        	System.out.println(array[1]);
		        }else{
		        	System.out.println(array[1].substring(0, index));
		        }
		    }
		}
		
		
		
		
	}
}
