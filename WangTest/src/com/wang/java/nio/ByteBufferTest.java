package com.wang.java.nio;

import java.nio.ByteBuffer;

public class ByteBufferTest {

	
	public static void main(String[] args) {
		test1();
	}
	
	public static void test1() {
	    ByteBuffer buffer = ByteBuffer.allocate(1024);
	  

	    buffer.put("abc".getBytes());
	    System.out.println(buffer.position());    //1024
	    buffer.put("def".getBytes());
	    System.out.println(buffer.position()); 
	    
	    byte[] dst = new byte[buffer.limit()-200]; 
		 
	    buffer.get(dst);
	    System.out.println(new String(dst));
	    
	    buffer.flip();
	    dst = new byte[buffer.limit()]; 
	 
	    buffer.get(dst);
	    
	    System.out.println(buffer.position());    //结果为3
	    buffer.flip();
	    System.out.println(buffer.position());     //结果为0
	    
	    System.out.println(new String(dst));       //答应"abc"
	}
}
