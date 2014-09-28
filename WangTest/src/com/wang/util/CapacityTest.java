package com.wang.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CapacityTest {

	
	public static void main(String[] args) {
		
        test2();
	}
	
	public static void test1 () {
		int size = 200000;
		List<TheData2> list = new ArrayList<TheData2>(size);
		
		for(int i=0; i<size; i++ ){
			TheData2 theData2 = new TheData2(i, String.valueOf(i), 2l, String.valueOf(i) );
			list.add(theData2);
		}
		
		System.out.println("ok");

	}
	
	public static void test2() {
		int size = 200000;
		List<TheData> list = new ArrayList<TheData>(size);
		
		
		for(int i=0; i<size; i++ ){
			TheData theData = new TheData(i);
			list.add(theData);
		}
		
		System.out.println("ok");
		
	}
	
	
	static class TheData2{
		Map<Integer, String> map1 = new HashMap<Integer, String>();
		Map<Integer, Long> map2 = new HashMap<Integer, Long>();
		Map<Integer, String> map3 = new HashMap<Integer, String>();
		
		public TheData2(int i, String a, long b, String c) {
			map1.put(i, a);
			map2.put(i, b);
			map3.put(i, c);
			
		}
		
		
		
	}
	
	static class TheData{
		Map<Integer, Data> map = new HashMap<Integer, Data>();

		public TheData(int i) {
			Data data = new Data(String.valueOf(i), i, String.valueOf(i));
			map.put(i, data);
		}
	}
	
	
	static class Data{
		private String a;
		private long b;
		private String c;
		
		public Data(String a, long b , String c) {
			this.a = a;
			this.b = b;
			this.c = c;
		} 
	}
}
