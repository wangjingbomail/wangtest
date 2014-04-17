package com.wang.util;

import java.util.ArrayList;
import java.util.List;

public class ListTest {

	public static void main(String[] args) {
//		test1();
		test2();
	}
	
	public static void test1() {
		ArrayList list = new ArrayList<Integer>();
		
		list.add(1);
		
		List list2 = list.subList(1, 1);
		
		System.out.println(list2);
	}
	
	public static void test2() {
		
		List<Integer> idList = new ArrayList<Integer>();
		
		for(int i=0; i<10; i++) {
			idList.add( i);
		}
		
		
		
		List<Integer> idList2 = new ArrayList<Integer>();
		idList2.addAll(idList.subList(4, 6));
		
		System.out.println("ok");
	}
}
