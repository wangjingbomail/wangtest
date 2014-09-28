package com.wang.util;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

public class PushCountTest {

	private static Map<String, Map<String, Long>> subidCountMap = new HashMap<String, Map<String, Long>>();

//	private static List<String> subidList = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception {
	    process();
	    output();
	    	    
//	    outputStatusStat();
        outputCommentStat();
	}
	
	private static void outputStatusStat() {
		List<String> subidList = new ArrayList<String>();
	    subidList.add("10167");
	    subidList.add("10277");
	    subidList.add("10080");
	    
	    outputCustomer(subidList);
	    subidList.clear();
	    
	    
	    subidList.add("10276");
	    subidList.add("10285");
	    subidList.add("10068");
	    subidList.add("10145");
	    subidList.add("10162");
	    subidList.add("10090");
	    subidList.add("10096");
	    outputCustomer(subidList);
	    
	}
	
	private static void outputCommentStat() {
		List<String> subidList = new ArrayList<String>();
	    subidList.add("10083");
	    subidList.add("10177");
	    subidList.add("10287");
	    
	    outputCustomer(subidList);
	    subidList.clear();
	    
	    
	    subidList.add("10286");
	    subidList.add("10155");
	    subidList.add("10069");
	    outputCustomer(subidList);
	}
	
	
	public static void process() throws Exception {
		RandomAccessFile file = new RandomAccessFile("comment.log","r");
		
		String str = null;
		
		while( (str=file.readLine()) !=null) {
		    String time = str.substring(11,19);
		    String jsonStr = str.substring(72);
		    
			JSONObject jsonObject = JSONObject.fromObject(jsonStr);
			
			Iterator<String> subidIterator = jsonObject.keys();
			
			while(subidIterator.hasNext()) {
				String subid = subidIterator.next();
				long value = jsonObject.getLong(subid);
				
				Map<String, Long> treeMap = subidCountMap.get(subid);
				if (treeMap==null) {
					treeMap = new TreeMap<String, Long>();
					subidCountMap.put(subid, treeMap);
					
				}
				
				Long oldValue = treeMap.get(time);
				
				if (oldValue==null) {
				    treeMap.put(time, value);
				}else{
					treeMap.put(time, value+oldValue);
				}
				
			}
			
		}
		
		
	}
	
	public static void output(){
	    Iterator<String> subidIterator = subidCountMap.keySet().iterator();
	    
	    while(subidIterator.hasNext()) {
	    	String subid = subidIterator.next();
	    	long total = 0l;
	    	System.out.println("    ------------------------------------------        ");
	    	System.out.println("    ------------------------------------------        ");
	    	System.out.println("    ------------------------------------------        ");
	    	System.out.println(subid);
	    	
	    	Map<String, Long> timeCountMap = subidCountMap.get(subid);
	    	
	    	Iterator<String> timeIterator = timeCountMap.keySet().iterator();
	    	
	    	while(timeIterator.hasNext()) {
	    		String time = timeIterator.next();
	    		Long value = timeCountMap.get(time);
	    		
	    		total +=value;
	    		//System.out.println( time + " " + value);
	    	}
	    	
	    	System.out.println(subid + " total: " + total);
	    	
	    	
	    }
	}
	
	/**
	 * 
	 * 输出某个客户的所有subid某个时间推送量的总和
	 */
	public static void outputCustomer(List<String> subidList) {
		System.out.println("----------------------");
		System.out.println("----------------------");
		System.out.println("----------------------");
		Map<String, Long> timeTotalMap = new TreeMap<String, Long>();
		
		long total = 0l;
		
		for(String subid:subidList) {
            Map<String, Long> timeCountMap = subidCountMap.get(subid);
	    	
	    	Iterator<String> timeIterator = timeCountMap.keySet().iterator();
	    	
	    	while(timeIterator.hasNext()) {
	    		String time = timeIterator.next();
	    		Long value = timeCountMap.get(time);
	    		total +=value;
	    		
	    		if (timeTotalMap.get(time)==null) {
	    			timeTotalMap.put(time, value);
	    		}else{
	    			timeTotalMap.put(time, timeTotalMap.get(time)+value);
	    		}
	    	}
		}
		
		System.out.println("total:" + total);
		
		Iterator<String> timeIterator = timeTotalMap.keySet().iterator();
		while(timeIterator.hasNext()) {
			String time = timeIterator.next();
			Long value = timeTotalMap.get(time);
			
			System.out.println(time + " " +value);
		}
		
		
		
	}
	
}
