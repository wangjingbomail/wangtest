package com.wang.util;

import java.util.ArrayList;
import java.util.List;

public class ObjectSizeTest {

	private int a;
//	private long k;
	private int b;
	private int c;
	private int d;
	private int e;
	private int g;
	private int h;
	private int i;
	private int j;
	private int k;
//	private byte[] byteArray = new byte[]{1,2,3,4,5,6,7,8,9,0};
	private ObjectSize2 objectSize2;

	
	
	public ObjectSizeTest(int a, int b) {
		this.a = a;
		this.b = b;
//		this.k = k;
		
	}
	
	public static void main(String[] args) throws Exception{
		List<ObjectSizeTest> list = new ArrayList<ObjectSizeTest>();
		for(int i=0; i<100000; i++) {
		    list.add( new ObjectSizeTest(1,1) );
		    System.out.println("size:" + ObjectSizeFetcher.getObjectSize( list.get(i)) );
		}
		
		ObjectSize2 objectSize2 = new ObjectSize2();
		System.out.println("size2:" + ObjectSizeFetcher.getObjectSize(objectSize2));
		
		Thread.sleep(1000000);

		System.out.println("abcd".getBytes().length);
		System.out.println(ObjectSizeFetcher.getObjectSize("abcd"));
	}
}
