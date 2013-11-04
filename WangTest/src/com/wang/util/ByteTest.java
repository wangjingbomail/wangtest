package com.wang.util;

public class ByteTest {

	public static void main(String[] args) {
   
        testByteValue();
        
	}
	
	public static void testByteValue() {
		String value = "00:00,01 \r\n";
		
		
		printByteArray(value.getBytes());
	}
	
	public static void testLength() {
	        String a= "a";
	        String ab = "ab";
	        String abc = "abc";
	        String abcd = "abcd";
	        String abcde = "中国";
	        
	        System.out.println(a.length() + " "  + a.getBytes().length);
	        System.out.println(ab.length() + " "  + ab.getBytes().length);
	        System.out.println(abc.length() + " "  + abc.getBytes().length);
	        System.out.println(abcd.length() + " "  + abcd.getBytes().length);
	        System.out.println(abcde.length() + " " + abcde.getBytes().length);
	}
	
	public static void printByteArray(byte[] byteArray) {
		int len = byteArray.length;
		for(int i=0; i<len; i++) {
			System.out.println(byteArray[i]);
		}
	}
	
	  
}
