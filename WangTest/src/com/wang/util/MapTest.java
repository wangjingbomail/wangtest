package com.wang.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MapTest {

    public static void main(String[] args) {
//		listtest();
//    	Map<String, String> map = new HashMap<String, String>();
//    	map.put("123", null);
    	
//    	treeMapTest();
    	
    	long a = Long.valueOf("1000000000000000000");
    	System.out.println(a);
    	Map<Integer, Integer> map = new HashMap<Integer, Integer>(4);
    	map.put(10050, 1);
    	
    	map.get(null);
    	map.remove(null);
    	System.out.println("ok");
    	
//    	System.out.println(map.containsKey(10050));
//    	System.out.println(map.containsKey(String.valueOf(10050)));

	}
    
    
    public static void treeMapTest() {
    	TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>();
    	
    	treeMap.put(123, "a");
    	treeMap.put(560, "abc");    	
    	
    	Entry<Integer, String>  entry = treeMap.floorEntry(450);
    	System.out.println(entry);
    	
    	Entry<Integer, String>  entry2 = treeMap.floorEntry(560);
    	System.out.println(entry2);
    	
    }
    
    public static void maptest1() {
        Map<String, String> map = new HashMap<String, String>();
		
		map.put("a", null);
		map.put("a", "b");
		map.get("c");
    }
    public static void listtest() {
    	List<Integer> list = new ArrayList<Integer>();
    	list.add(1);
    	list.add(2);
    	
    	System.out.println(list.subList(0, 1));
    	
    	
    }
}
