package com.wang.java.nio;

import org.apache.mina.common.ByteBuffer;



public class ByteBufferMinaTest {

	
	public static void main(String[] args) {
//		test1();
//		testPartGet();
		
		testPut();
	}
	
	
	
	public static void testPut() {
		ByteBuffer buffer = ByteBuffer.allocate(1020);
		 
		byte[] byteArray = new byte[1020];
		for(int i=0; i<1020; i++ ) {
			byteArray[i] = (byte)(i %127);
		}
		System.out.println( "length:" + byteArray.length );
		
		for(int i=0; i<100; i++) {
			
			System.out.println(buffer.limit() + " " + buffer.position());
			buffer.put(byteArray);
			System.out.println("after " + buffer.limit() + " " + buffer.position());
			buffer.flip();
			buffer.get();
			System.out.println("after get " + buffer.limit() + " " + buffer.position());
			buffer.clear();
			System.out.println("after clear " + buffer.limit() + " " + buffer.position());
			
			
		}
		

	}
	
	public static void testPartGet() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		for(int i=0; i<100; i++) {
		    buffer.put(String.valueOf(i).getBytes());
		}
		
		buffer.flip();
		
		
		for(int i=0; i<101; i++) {
			int len = String.valueOf(i).length();
		    byte[] byteArray = new byte[len];	
		    
		    buffer.get(byteArray, 0 , len);
		    System.out.println(new String(byteArray));
		}
		
	}
	
	
	public static void test1() {
	    ByteBuffer buffer = ByteBuffer.allocate(1024);
	  

	    buffer.put("abc".getBytes());
	    System.out.println(buffer.position());    //3
	    buffer.put("def".getBytes());
	    System.out.println(buffer.position());    //6
	    System.out.println(buffer.limit());
	    
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
