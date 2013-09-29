package com.wang.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapTest {

    public static void main(String[] args) {
		listtest();
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
