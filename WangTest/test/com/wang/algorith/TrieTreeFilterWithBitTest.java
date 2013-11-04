package com.wang.algorith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import com.wang.algorithm.TrieStringFilter;

public class TrieTreeFilterWithBitTest {

	@Test
	public void test1() {
		List<String> list = new ArrayList<String>();
		list.add("ibm");
		list.add("a");
		list.add("oracle");
		list.add("中国");
		list.add("新浪");
		list.add("zhongguancun");
		list.add("北京");
		list.add("b");
		list.add("中");
		
		TrieStringFilter filter = new TrieStringFilter();
		filter.build(list);
		
		Assert.assertTrue(filter.match("ibm"));
		Assert.assertTrue(filter.match("a"));
		Assert.assertTrue(filter.match("oracle"));
		Assert.assertTrue(filter.match("中国"));
		Assert.assertTrue(filter.match("新浪"));
		Assert.assertTrue(filter.match("zhongguancun"));
		Assert.assertTrue(filter.match("北京"));
		Assert.assertTrue(filter.match("b"));
		Assert.assertTrue(filter.match("中"));
		
		Assert.assertFalse(filter.match("新"));
				
		
	}
	
	@Test
	public void test2() {
		List<String> characterList = new ArrayList<String>(3000);
		try {
		    File file=new File("Word.txt");
		    BufferedReader reader=new BufferedReader(new FileReader(file));
		    String text;
		    while( ( text=reader.readLine() )!=null ) {
		      
		    	String[] array = text.split(" ");
		        
		        if (array!=null) {
		            int length = array.length;
		            for(int i=0; i<length; i++) {
		            	if (array[i]!=null && array[i].startsWith(" ")==false && array[i].length()>=1 ) {
		            	    characterList.add(array[i]);
		            	}
		            }
		        }
		    }
		}catch(Exception e) {
		    System.out.println("exception:" + e);	
		}
		
		for(int i=0; i<26; i++) {
			characterList.add(String.valueOf( (char)('a'+i) ));
		}
		
		long begin = System.currentTimeMillis();
		 List<String> wordList = getRandomList(characterList, 10000);
	    TrieStringFilter filter = new TrieStringFilter();
	    filter.build(wordList);
	    System.out.println("ok");
	    
	    
	}
	
		/**
	 * 根据字返回构造后的内容,返回的内容中，2个到5个字的占
	 * @param characterList
	 * @param size   返回字的大小
	 * @return
	 */
	private List<String> getRandomList(List<String> characterList, int size){
		
		List<String> resultList = new ArrayList<String>(size);
		
		long seed = System.currentTimeMillis();
			
		Random random = new Random(seed);
		
		int length = characterList.size();
		for(int i=0; i<size; i++) {
			StringBuilder strBuilder = new StringBuilder();
			int rand = random.nextInt(12);
			for(int j=0; j<rand; j++) {
			    int rand1 = random.nextInt(length);
			    
			    strBuilder.append(characterList.get(rand1));
			}
			
			if (strBuilder.length()>=1) {
			    resultList.add(strBuilder.toString());
			}
		}
		
		
		return resultList;
		
		
	}
	
}
