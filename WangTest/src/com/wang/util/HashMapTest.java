package com.wang.util;

import java.util.HashMap;
import java.util.Map;

public class HashMapTest extends HashMap {

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		
		for(int i=0; i<454; i++) {
			map.put("test"+i, String.valueOf(i));
		}
		
		String value = "test42";
		for(int i=0; i<1000; i++) {
		    long begin = System.nanoTime();
		    int hashCode = value.hashCode();
		    long end = System.nanoTime();
		 
		    System.out.println(" hashCode " + (end-begin));
		    
		    long begin1 = System.nanoTime();
		    hash(hashCode);
		    long end1 = System.nanoTime();

		    System.out.println(" hash " + (end1-begin1));
		    
		    long begin2 = System.nanoTime();
		    map.get("ok34");
		    long end2 = System.nanoTime();
		    
		    System.out.println(" map " + (end2-begin2));
		    
		    
		}
	}
	
	
	
	 static int hash(int h) {
	        // This function ensures that hashCodes that differ only by
	        // constant multiples at each bit position have a bounded
	        // number of collisions (approximately 8 at default load factor).
	        h ^= (h >>> 20) ^ (h >>> 12);
	        return h ^ (h >>> 7) ^ (h >>> 4);
	    }
}
