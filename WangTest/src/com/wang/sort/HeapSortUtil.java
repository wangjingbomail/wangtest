package com.wang.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HeapSortUtil {
	
	private final static int NO_VALUE = -1;
	/**
	 * 这个是假定map.list中的数据是无序的
	 * map中key为uid,value为该用户所发表的微博的id(feed_id)
	 * 返回值：按照feed_id从大到小排序，返回第start个开始的size个feed_id.
	 * start从0开始
	 * 
	 * 如果没有合适的值，则返回空列表
	 * @return
	 */
	public static List<Long> getMaxList(Map<Long, List<Long>> map, int start, int size) {
		
		if (start<0 || size<=0) {
			return new ArrayList<Long>();
		}
		
		List<Long> resultList = new ArrayList<Long>(size);
		
		List<HeapSort> sortList = buildHeapSort(map);
		
	
		int order = 0;
		while( order< (start+size) ) {
			Long max = getMax(sortList);
			
			if (order >= start) {
			    resultList.add(max);	
			}
			
			order++;
			
			
		}
		
		return resultList;
	}
	
	
	/**
	 * 假定map.list的数据是有序的，且从大到小。
	 * @param map
	 * @param start
	 * @param size
	 * @return
	 */
	public static List<Long> getMaxListFromOrderedList(Map<Long, List<Long>> map, int start, int size) {
	       //记录map中的list中现在读取到哪个值了,如果value为-1,则表示key所对应的List中已经没有值了
		Map<Long, Integer> posMap = initPosMap(map);
	    
	    List<Long> resultList = new ArrayList<Long>();
	    
	    HeapSort heapSort = buildTopHeapSort(map);
	    
		int order = 0;
		while( order< (start+size) ) {
			Long max = heapSort.remove();
		
			if (order >= start) {
			    resultList.add(max);	
			}
			
			
		    Long appendNum = getNext(map, posMap, max);
		    if (start==100) System.out.println(appendNum);
		    if (appendNum!=null) {
		    	heapSort.put(appendNum);
		    }
			
			order++;
			
			
		}
	    
	  
	    return resultList;
		
		
	}
	
	/**
	 * 从removedMax所属的list中取得一个值，该值将填充到heapsort中
	 * 如果removedMax所属的list中没有值了，则返回null。
	 * @param map
	 * @param posMap
	 * @param removedMax
	 * @return
	 */
	private static Long getNext(Map<Long, List<Long>> map, Map<Long, Integer> posMap, Long removedMax) {
		
		
		Iterator<Long> keyIterator = posMap.keySet().iterator();
		while(keyIterator.hasNext()) {
			Long key = keyIterator.next();
			int pos = posMap.get(key);
			
			if ( pos == NO_VALUE) {
				continue;
			}
			
			Long num = map.get(key).get(pos);
			
			if (num!=null && num==removedMax) {
				pos++;
				
				if (map.get(key).size()< (pos+1)) {
					posMap.put(key, NO_VALUE);
					return null;
				}else{
					posMap.put(key, pos);
					return map.get(key).get(pos);
				}
				

			}

			
		}
		
		return null;

	}
	
	/**
	 * 初始化posMap
	 * @param map
	 * @return
	 */
	private static Map<Long, Integer> initPosMap(Map<Long, List<Long>> map) {
	    Iterator<Long> keyIterator = map.keySet().iterator();
	    Map<Long, Integer> posMap = new HashMap<Long, Integer>();
	    
	    while(keyIterator.hasNext()) {
	    	Long key = keyIterator.next();
	    	
	    	List<Long> list = map.get(key);
	    	
	    	if (list==null || list.isEmpty()==true) {
	    	    posMap.put(key, NO_VALUE);
	    	}else{
	    		posMap.put(key, 0);
	    	}
	    }		
	    
	    return posMap;
	}
	
	
	
	private static HeapSort buildTopHeapSort(Map<Long, List<Long>> map) {
	       //准备放到
	    List<Long> idList = new ArrayList<Long>(map.size());
	    
	    Iterator<Long> keyIterator = map.keySet().iterator();
	    
	    while(keyIterator.hasNext()) {
	    	Long key = keyIterator.next();
	    	
	    	List<Long> list = map.get(key);
	    	
	    	if (list!=null && list.isEmpty()==false) {
	    	    idList.add(list.get(0));	
	    	}
	    }
	    
	    HeapSort heapSort = new HeapSort(idList);
	    
	    return heapSort;
	}
	
	
	
	/**
	 * 构建map.size()个大顶堆
	 * @param map
	 * @return
	 */
	private static List<HeapSort> buildHeapSort(Map<Long, List<Long>> map){
		
		int mapSize = map.size();
		
		List<HeapSort> sortList = new ArrayList<HeapSort>(mapSize);
		
		/**
		 * 构建mapSize个大顶堆
		 */
		Iterator<Long> keyIterator = map.keySet().iterator();
		while(keyIterator.hasNext()) {
			Long key = keyIterator.next();
			List<Long> list = map.get(key);
			
			if (list!=null && list.isEmpty()==false) {
				HeapSort heapSort = new HeapSort(list);
				sortList.add(heapSort);
			}
		}
		
		return sortList;
		
		
	}
	
	
	/**
	 * 得到其中一系列大顶堆中最大的一个值，并调整堆
	 * @param sortList
	 * @return
	 */
	private static Long getMax(List<HeapSort> sortList) {
		
		int theHeapNum = 0;  //最大值所属的堆在sortList中的编号
		Long max = sortList.get(theHeapNum).peek();
		
		int length = sortList.size();
		for(int i=1; i<length; i++) {
			Long num = sortList.get(i).peek();
			
			if (num==null) {
				continue;
			}
			
			if (max==null || max<num) {
				max = num;
				theHeapNum = i;
			}
		}
		
		if (max!=null) {
			sortList.get(theHeapNum).remove();
		}
		
		return max;
		
	}
	
	
}
