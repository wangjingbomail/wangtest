package com.wang.util.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.wang.sort.HeapSortUtil;

/**
 * 堆排序的类
 * @author wang
 *
 */
public class HeapSortUtilTest {


	private  Map<Long, List<Long>> map;
	private int MAX_DATA_SIZE = 20000;  //总共的数据的大小
	private int MAX_MAP_SIZE = 100;   //map的大小
	
	@Before
	public void prepareData() {	
		
		map = new HashMap<Long, List<Long>>();
		for(int i=MAX_DATA_SIZE-1; i>=0; i--) {
		    int key = i%MAX_MAP_SIZE;
		    List<Long> list = map.get(Long.valueOf(key) );
		    
		    if (list==null) {
		    	list = new ArrayList<Long>();
		    	map.put(Long.valueOf(key), list);
		    }
		    
		    list.add(Long.valueOf(i));
		}
				
		
	}
	
	
	@Test 
	public void getMaxListTest() {
		int start =0;
		int size = 25;
		List<Long> list = HeapSortUtil.getMaxList(map, start, size);
		Assert.assertTrue( validate(list, start, size) );
		
	}
	
	@Test 
	public void getMaxListTest1() {
		int start =25;
		int size = 25;
		List<Long> list = HeapSortUtil.getMaxList(map, start, size);
		Assert.assertTrue( validate(list, start, size) );
		
	}
	
	@Test
	public void getMaxListTest2() {
		int start =0;
		int size = 50;
		List<Long> list = HeapSortUtil.getMaxList(map, start, size);	
		Assert.assertTrue( validate(list, start, size) );
	}
	
	@Test
	public void getMaxListTest3(){
	    int start =10;
		int size = 50;
		List<Long> list = HeapSortUtil.getMaxList(map, start, size);	
		Assert.assertTrue( validate(list, start, size) );
	}
	
	@Test
	public void getMaxListTest4() {
		int start =100;
		int size = 50;
		List<Long> list = HeapSortUtil.getMaxList(map, start, size);	
		Assert.assertTrue( validate(list, start, size) );
	}
	
	@Test
	public void getMaxListTest5(){
		int start =9990;
		int size = 10;
		List<Long> list = HeapSortUtil.getMaxList(map, start, size);	
		Assert.assertTrue( validate(list, start, size) );
		
	}
	
	@Test
	public void getMaxListTest6(){
		int start = 0;
		int size = MAX_DATA_SIZE;
		List<Long> list = HeapSortUtil.getMaxList(map, start, size);	
		Assert.assertTrue( validate(list, start, size) );
		
	}
	
	@Test 
	public void getMaxListFromOrderedList() {
		int start =0;
		int size = 25;
		List<Long> list = HeapSortUtil.getMaxListFromOrderedList(map, start, size);
		Assert.assertTrue( validate(list, start, size) );		
	}
	
	@Test 
	public void getMaxListFromOrderedList1() {
		int start = 25;
		int size = 25;
		List<Long> list = HeapSortUtil.getMaxListFromOrderedList(map, start, size);
		Assert.assertTrue( validate(list, start, size) );		
	}
	
	
	@Test
	public void getMaxListFromOrderedList2() {
		int start =0;
		int size = 50;
		List<Long> list = HeapSortUtil.getMaxListFromOrderedList(map, start, size);	
		Assert.assertTrue( validate(list, start, size) );
	}
	
	@Test
	public void getMaxListFromOrderedList3(){
	    int start =10;
		int size = 50;
		List<Long> list = HeapSortUtil.getMaxListFromOrderedList(map, start, size);	
		Assert.assertTrue( validate(list, start, size) );
	}
	
	@Test
	public void getMaxListFromOrderedList4() {
		int start =100;
		int size = 50;
		List<Long> list = HeapSortUtil.getMaxListFromOrderedList(map, start, size);	
		Assert.assertTrue( validate(list, start, size) );
	}
	
	@Test
	public void getMaxListFromOrderedList5(){
		int start =9990;
		int size = 10;
		List<Long> list = HeapSortUtil.getMaxListFromOrderedList(map, start, size);	
		Assert.assertTrue( validate(list, start, size) );
		
	}
	
	@Test
	public void getMaxListFromOrderedList6(){
		int start = 0;
		int size = MAX_DATA_SIZE;
		List<Long> list = HeapSortUtil.getMaxListFromOrderedList(map, start, size);	
		Assert.assertTrue( validate(list, start, size) );
		
	}
	
	/**
	 * 校验从map中getMaxList得到的list是否正确
	 * @param list  从map中getMaxList得到的list
	 * @param start
	 * @param size
	 * @return
	 */
	private boolean validate(List<Long> list, int start, int size) {
		
		if (list.size()!=size) {
			return false;
		}
		
		for(int i=0; i<size; i++) {
			int num = MAX_DATA_SIZE-start-i-1;
			
			if ( list.get(i)!=num ) {
				return false;
			}
		}
		
		return true;
	}
}
