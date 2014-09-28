package com.wang.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * MapReduce算法
 * @author jingbo7
 *
 */
public class MapReduce {
	
	private static AtomicInteger mapFinisedNum = new AtomicInteger(0);  //map任务完成的个数
	private static AtomicInteger reduceFinisedNum = new AtomicInteger(0);   //reduce任务的个数
	
	private static int partitionNum = 100;
	
	//map后，shuffle好的结果
	private static List<Map<Integer, List<UrlObject>>> shuffleList = new ArrayList<Map<Integer, List<UrlObject>>>(partitionNum);
	
	//reduce任务后的结果
	private static List<Map<String, UrlObject>> reducedList = new ArrayList<Map<String, UrlObject>>(partitionNum);
	
	
	public static void main(String[] args) throws Exception {
		
		String[] array = prepareData();
		
		int step = 10000;
		int count = 0;
		for(int i=0; i<1000000; i+=step) {
			
			Thread thread = new Thread(new MapJob(array, i, i+step-1, count));
			thread.start();
			count ++;
		}
		
		//等待map任务全部完成
		while(mapFinisedNum.get() < partitionNum) {
			Thread.sleep(100);
		}
		
		for(int i=0; i<partitionNum; i++) {
			ReduceJob reduceJob = new ReduceJob(i);
			reduceJob.run();
			
		}
		
		//等待reduce任务全部完成
		while(reduceFinisedNum.get() < partitionNum) {
			Thread.sleep(100);
		}
		
		Map<String, UrlObject> resultMap = merge(reducedList);
		
		
//		System.out.println(resultMap );
		
		for(int i=0; i<100; i++) {
		    UrlObject urlObject = resultMap.get(i+"");
		    
		    if (urlObject==null) {
		    	System.out.println("empty:" + i);
		    	continue;
		    }
		    if (urlObject.time!= i*10000){
		    	System.out.println(" not correct:" + i);
		    }
		    
		    if (urlObject.count!= 10000){
		    	System.out.println(" not correct:" + i);
		    }
		}
		
		
		
		
	}
	
	

	
	public static String[] prepareData() {
		String[] array = new String[1000000];
		
		for(int i=0; i<1000000; i++) {
			String str = "0--" + i%100 + "--1.1.1.1--" + i%100;
			array[i]=str;
		}
		
		return array;
		
	}
	
	/**
	 * 把一行一行的数据专程对象
	 * @param array
	 * @return
	 */
	public static  List<UrlObject> map(String[] array, int beginPos, int endPos) {
		
		List<UrlObject> list = new ArrayList<UrlObject>(array.length);
		for(int i=beginPos; i<=endPos; i++) {
			list.add(UrlObject.fromStr(array[i]));
		}
		
		return list;
	}
	
	
	/**
	 * 在同一个partition，key相同的数据合并
	 * @param list
	 * @return
	 */
	public static  Map<String, UrlObject> combine(List<UrlObject> list) {
		Map<String, UrlObject> map = new HashMap<String, UrlObject>(list.size());
		
		for(UrlObject url:list) {
			UrlObject targetUrl = map.get(url.url);
			if (targetUrl==null ) {
				targetUrl = url;
				map.put(url.url, targetUrl);
				
				continue;
			}
			
			targetUrl.addTimeCount(url.time, url.count);
			
		}
		
		return map;
	}
	
	
	/**
	 * 把数据进行shuffle，根据partitionNum来进行
	 * @return
	 */
	public static  Map<Integer,List<UrlObject>> shuffleWrite(Map<String, UrlObject> map, int partitionNum) {
		
		Map<Integer, List<UrlObject>> resultMap = new HashMap<Integer, List<UrlObject>>(partitionNum);
		
		Iterator<String> keyIterator = map.keySet().iterator();
		
		while(keyIterator.hasNext()) {
			String url = keyIterator.next();
			
			Integer hashCode = url.hashCode() % partitionNum;
			UrlObject urlObject = map.get(url);
			
			List<UrlObject> list = resultMap.get(hashCode);
			
			if (list==null) {
				list = new LinkedList<UrlObject>();
				resultMap.put(hashCode, list);
			}
			
			list.add(urlObject);
		}
		
		
		return resultMap;
	}
	
	
	public  static List<UrlObject> shuffleRead(Map<Integer,List<UrlObject>> map, int partitionNum) {
		
		return map.get(partitionNum);
	
	}
	
	
	public static Map<String, UrlObject> reduce(List<List<UrlObject>> wholeList) {
		
		Map<String, UrlObject> map = new HashMap<String, UrlObject>();
		
		for(List<UrlObject> list:wholeList) {
			for(UrlObject urlObject: list) {
				String url = urlObject.url;
				
				UrlObject targetUrlObject = map.get(url);
				
				if (targetUrlObject==null) {
					
					
					map.put(url, urlObject);
					continue;

					
				}
				
				targetUrlObject.addTimeCount(urlObject.time, urlObject.count);
			}
		}
		
		return map;
	}
	
	
	public static Map<String, UrlObject> merge(List<Map<String, UrlObject>> list) {
		Map<String, UrlObject> map = new HashMap<String, UrlObject>();
		
		for(Map<String, UrlObject> theMap:list) {
		    	
		    Iterator<String> iterator = theMap.keySet().iterator();
		    while(iterator.hasNext()) {
		    	String url = iterator.next();
		    	UrlObject urlObject = theMap.get(url);
		    	
		    	
		    	UrlObject targetObject = map.get(url);
		    	if (targetObject==null){
		    		map.put(url, urlObject);
		    		continue;
		    	}
		    	
		    	targetObject.addTimeCount(urlObject.time, urlObject.count);
		    	
		    	
		    }
			
		}
		
		return map;
	}

	
	public static class MapJob implements Runnable{
		
		private String[] array;
		private int beginPos;
		private int endPos;
		private int partitionNo;
		
		public MapJob(String[] array, int beginPos, int endPos, int partitionNo) {
			this.array = array;
		    this.beginPos = beginPos;
		    this.endPos = endPos;
		    this.partitionNo = partitionNo;
		}
		
		@Override
		public void run() {
			List<UrlObject> list = map(array, beginPos, endPos);
			
			Map<String, UrlObject> combinedMap = combine(list);
			
			Map<Integer, List<UrlObject>> resultMap = shuffleWrite(combinedMap, partitionNum);
			
			//放到中间结果中
			shuffleList.add(partitionNo, resultMap);
			
			mapFinisedNum.incrementAndGet();
			
		}
	}
	
	public static class ReduceJob implements Runnable{
		private int partitionNo;
		
		public ReduceJob(int partitionNo ) {

			this.partitionNo = partitionNo;
		}
		
		@Override
		public void run() {
			
			List<List<UrlObject>> toBeReduceDList = new ArrayList<List<UrlObject>>(partitionNum);
			for(Map<Integer, List<UrlObject>> map: shuffleList){
				
				List<UrlObject> list = map.get(partitionNo);
				if (list!=null) {
				    toBeReduceDList.add(list);
				}
			}
			
			Map<String, UrlObject> map = reduce(toBeReduceDList);
			reducedList.add(partitionNo, map);
			
			reduceFinisedNum.incrementAndGet();
		}
	} 
	
	public static class UrlObject{
		
		public String url;
		public long time=0l;
		public int count=0;
		
		public void addTimeCount(long time, int count ) {
			this.time += time;
			this.count += count;
		}
		
		@Override
		public String toString() {
			return "url:" + url + " time:" + time + " count:" + count;
		}
		
		public static UrlObject fromStr(String str) {
			try {
			    String[] array = str.split("--");
				UrlObject url = new UrlObject();
				url.url=array[1];
				url.time= Long.valueOf(array[3]);
				url.count = 1;
				
			    return url;
			}catch(Exception e) {
				return null;
			}
			
			
		}
		
	}
}

