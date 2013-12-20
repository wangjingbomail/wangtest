package com.wang.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {

	public static void main(String[] args) {
		test1();
	}
	
	public static void test1() {
		
		Pattern pattern = Pattern.compile(",\\d{2,}?ms");
		Matcher matcher = pattern.matcher(",123ms");
		System.out.println(matcher.matches());
		
		                                   
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
}
