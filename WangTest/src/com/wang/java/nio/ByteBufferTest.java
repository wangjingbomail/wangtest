package com.wang.java.nio;

import java.nio.ByteBuffer;

public class ByteBufferTest {

	
	public static void main(String[] args) {
//		test1();
//		testPartGet();
		
//		testPut();
//		testArray();
		testPutAndGet();
	}
	
	
	
	/**
	 * 测试array方法
	 */
	public static void testArray() {
		ByteBuffer byteBuffer = ByteBuffer.allocate(10);
		byteBuffer.put((byte)1);
		byteBuffer.put((byte)2);
		
		
		byte[] byteArray = byteBuffer.array();
		
		System.out.println(byteArray.length);   //为10，也就是尽管byteBuffer中只有两个byte,但是array会导出所有的
	
		byteBuffer.flip();
		int len = byteBuffer.limit();
		byte[] byteArray1 = new byte[len];
	    byteBuffer.get(byteArray1, 0, len);
	    
	    System.out.println(byteArray1[0] + " " + byteArray1[1]);
	}
	
	
	public static  byte[] getByteArray(ByteBuffer byteBuffer) {
        int len = byteBuffer.limit();
	    byte[] byteArray = new byte[len];
	    byteBuffer.get(byteArray, 0, len);
	    
	    return byteArray;
	}
	public static void testPut() {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		
		for(int i=0; i<100; i++) {
			
			System.out.println(" is direct" + buffer.isDirect());
			System.out.println(buffer.limit() + " " + buffer.position());
			buffer.put("abcdefghij".getBytes());
			System.out.println("after " + buffer.limit() + " " + buffer.position());
			buffer.flip();
			buffer.get();
			System.out.println("after get " + buffer.limit() + " " + buffer.position());
			buffer.clear();
			System.out.println("after clear " + buffer.limit() + " " + buffer.position());
			
			
		}
		

	}
	
	public static void testPutAndGet() {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		
		buffer.put((byte)1);
		buffer.put((byte)2);
		
		buffer.flip();
		
		buffer.get();
		buffer.get();
		
		printBuffer(buffer);
		
//		buffer.flip();
		buffer.clear();
		
		printBuffer(buffer);
		
		
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
	
	private static void printBuffer(ByteBuffer buffer) {
		byte[] array = buffer.array();
		
		int len = array.length;
		for(int i=0; i<len; i++){
			System.out.print( array[i]);
		}
		
		System.out.println(" ");
	}

}
