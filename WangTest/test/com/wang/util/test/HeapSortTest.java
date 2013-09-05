package com.wang.util.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.wang.sort.HeapSort;

public class HeapSortTest {
	private  List<Long> idList;
	private int MAX_DATA_SIZE = 10000;  //总共的数据的大小
	
	@Before
	public void prepareData() {
		idList = new ArrayList<Long>();


		Random random = new Random(578925);
		for (int i=0; i<MAX_DATA_SIZE; i++) {
			idList.add(Long.valueOf(random.nextInt(MAX_DATA_SIZE)));
		}
		
		
				
		
	}
	
	@Test
	public void peekTest() {
        HeapSort heapSort = new HeapSort(idList);
        
        Long beforeNum = heapSort.remove();
        while(beforeNum!=null) {
        	
        	Long num1 = heapSort.peek();
        	Long num2 = heapSort.remove();
        	if (num2 != null) {
        	    Assert.assertTrue(num1.equals(num2));
        	    Assert.assertTrue( beforeNum>=num2 );
        	}
        	beforeNum = num2;

           
        }
       
	}
}
