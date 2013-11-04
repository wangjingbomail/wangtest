package com.wang.algorith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import com.wang.algorithm.TrieStringFilter;
import com.wang.algorithm.TrieStringFilterWithArray;
import com.wang.util.WangLogger;


/**
 * 测试字典树
 * @author wang
 *
 */
public class TrieTreeFilterTest {

	
	public static void main(String[] args) {
		List<String> wordList = new ArrayList<String>();
		wordList.add("ab");
		wordList.add("abc");
		wordList.add("fb");
		
		TrieStringFilterWithArray filter = new TrieStringFilterWithArray();
		filter.build(wordList);
		
		System.out.println(filter.getNodeCount());
	}
	
//	public void 
	
//	@Test
	public void functionTest() {


		List<String> characterList = getCharacterList();
		System.out.println("ok");
//		int max = 1;
//		for(int i=0; i<max; i++) {
//			System.out.println("max:" + max + " i: " + i);
	        List<String> wordList = getRandomList(characterList, 10000);
	        long begin = System.currentTimeMillis();
	        TrieStringFilterWithArray filter = new TrieStringFilterWithArray();
	        filter.build(wordList);
		
	        long end = System.currentTimeMillis();
	        
	        long consume = end-begin;
//	        if (consume>50) {
//	        	System.out.println(">build consume:" + consume);
//	        }else{
//	        	System.out.println("<build consume:" + consume);
//	        }
	        System.out.println("node count:" + filter.getNodeCount());
	        String word1 = "ibm";
//	        Assert.assertFalse(filter.match(word1));
	        

//	        for(String word:wordList){
//	        	Assert.assertTrue(filter.match(word));
//	        }
	        
	      
	        
//	        Assert.assertTrue( consume<5);
	        
//	        List<String> addWordList = getRandomList(characterList, 100);
//	        for(String word:addWordList) {
//	           begin = System.currentTimeMillis();
//	           filter.addWord(word);
//	           end = System.currentTimeMillis();
//	           
//	           if ( (end-begin) >= 2) {
//	               System.out.println("add consume:" + (end-begin));
//	           }
//	        }
	        
//		}
		
	}
	
	
//	@Test
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
	
//	@Test
	public void testAdd() {
		List<String> characterList = getCharacterList();
		
		for(int i=0; i<26; i++) {
			characterList.add(String.valueOf( (char)('a'+i) ));
		}
		
		long begin = System.currentTimeMillis();
		List<String> wordList = getRandomList(characterList, 200);
	    TrieStringFilter filter = new TrieStringFilter();
	    filter.build(wordList);
	    System.out.println("ok");
	    
        for(String word:wordList){
    	    Assert.assertTrue(filter.match(word));
        }
	    
	    
	}
	
	@Test
	public void testDelete() {
		List<String> characterList = getCharacterList();
		
		for(int i=0; i<26; i++) {
			characterList.add(String.valueOf( (char)('a'+i) ));
		}
		
		    //字不重复
		List<String> wordList = getRandomListWithoutDuplicate(characterList, 10000);
	    TrieStringFilter filter = new TrieStringFilter();
	    filter.build(wordList);
	    
	    WangLogger.info(" keyword_num:" + filter.getKeyword_num());
	    int keyword_num = filter.getKeyword_num();
        for(String word:wordList){

        	Assert.assertTrue( filter.match(word) );
    	    Assert.assertTrue( filter.match(word + "abcde中") );
    	    
    	    keyword_num--;
    	    filter.deleteWord(word);
    	    Assert.assertEquals(keyword_num, filter.getKeyword_num());
    	    
    	  
    	    Assert.assertFalse( filter.matchWholeWord(word) );
        }
        
	}
	
	
//	@Test
	public void testDeleteWithFixedList() {
		List<String> characterList = getCharacterList();
		
		for(int i=0; i<26; i++) {
			characterList.add(String.valueOf( (char)('a'+i) ));
		}
		
		    //字不重复
		List<String> wordList = getFixedListWithoutDuplicate(characterList, 10000);
	    TrieStringFilter filter = new TrieStringFilter();
	    filter.build(wordList);
	    
	    WangLogger.info(" keyword_num:" + filter.getKeyword_num());
	    int keyword_num = filter.getKeyword_num();
        for(String word:wordList){
        	WangLogger.info(" word:" + word);
    	    Assert.assertTrue( filter.match(word) );
    	    Assert.assertTrue( filter.matchWholeWord(word));
    	    Assert.assertTrue( filter.match(word + "abcde中") );
    	    filter.deleteWord(word);
    	    keyword_num = keyword_num -1;
    	    Assert.assertEquals(keyword_num, filter.getKeyword_num());
    	    WangLogger.info(" keyword_num:" + filter.getKeyword_num());
    	    
    	    
    	    Assert.assertFalse( filter.match(word) );
        }
        
	}
	
	@Test
	public void testMatchSpeed() {
		
		long time_consume = 0;
		int max_times = 1000;
		for(int times=0; times<max_times; times++) {
			List<String> characterList = getCharacterList();
			
			for(int i=0; i<26; i++) {
				characterList.add(String.valueOf( (char)('a'+i) ));
			}
			
			    //字不重复
			int max_keyword = 20000;
			List<String> wordList = getRandomListWithoutDuplicate(characterList, 20000);
		    TrieStringFilter filter = new TrieStringFilter();
		    filter.build(wordList);
			Assert.assertEquals(max_keyword, filter.getKeyword_num());
		    
		    String text = "一位内部人士饭局上告诉我，内部悲观预测，明年央视有可能出现1980年以来首次广告收入零增长！这是历史性的，而且2014年广告招标预售情况也不太理想,abc  racea dafa！";
	        long begin1 = System.currentTimeMillis();
		    long begin = System.nanoTime();
	        filter.match(text);
	        long end = System.nanoTime();
	        long end1 = System.currentTimeMillis();
	        WangLogger.info("time consume: " + 1.0*(end-begin)/1000000 + "ms consume2: " + (end1-begin1) + "ms");
	        time_consume += (end-begin);
	        
		}
		
		WangLogger.info("average time:" + 1.0*time_consume/max_times);
	    
	}
	
//	@Test
	public void test3() {
		
		List<String> characterList = getCharacterList();
		List<String> wordList = getFixedList(characterList,1000);
		TrieStringFilter filter = new TrieStringFilter();
		filter.build(wordList);
		
		for(String word:wordList) {

			Assert.assertTrue(filter.match(word));
		}
		
	}
	
	/**
	 * 得到固定的一个值
	 * @param characterList
	 * @param size
	 * @return
	 */
	private List<String> getFixedList(List<String> characterList, int size){
		
		List<String> resultList = new ArrayList<String>(size);
		
		int length = characterList.size()>size?size:characterList.size();
		
		for(int i=0; i<length-1; i++) {		
			resultList.add(characterList.get(i)+characterList.get(i+1));
		}
		
		return resultList;
		
	}

	
	/**
	 * 得到固定的一个值, 并且没有重复值
	 * @param characterList
	 * @param size
	 * @return
	 */
	private List<String> getFixedListWithoutDuplicate(List<String> characterList, int size){
		
		HashSet<String> wordSet = new HashSet<String>();
		List<String> resultList = new ArrayList<String>(size);
		
		int length = characterList.size()>size?size:characterList.size();
		
		for(int i=0; i<length-1; i++) {		
			String word = characterList.get(i)+characterList.get(i+1);
			if (wordSet.contains(word)==false) {
			    resultList.add(characterList.get(i)+characterList.get(i+1));
			    wordSet.add(word);
			}
		}
		
		return resultList;
		
	}
	
	/**
	 * 根据字返回构造后的内容,返回的内容中，没有重复的字
	 * @param characterList
	 * @param size
	 * @return
	 */
	private List<String> getRandomListWithoutDuplicate(List<String> characterList, int size) {
		List<String> resultList = new ArrayList<String>(size);
		
		long seed = System.currentTimeMillis();
			
		Random random = new Random(seed);
		
		HashSet<String> wordSet = new HashSet<String>();
		int count = 0;
		int length = characterList.size();
		while(true) {
			
			StringBuilder strBuilder = new StringBuilder();
			int rand = random.nextInt(12);
			for(int j=0; j<rand; j++) {
			    int rand1 = random.nextInt(length);
			    
			    strBuilder.append(characterList.get(rand1));
			}
			
			if (strBuilder.length()>=1) {
				String word = strBuilder.toString();
				
				if (wordSet.contains(word)==false) {
			        wordSet.add( word );
			        resultList.add( word ); 
			        count++;
				}
			}
			
			if ( count > (size-1) ) {
				break;
			}
		}
		
		return resultList;
		
	
	
	}
	
	/**
	 * 根据字返回构造后的内容,返回的内容中，可能会有重复数据
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
	
	/**
	 * 得到一条微博
	 * @param characterList
	 * @return
	 */
	private String getStatus(List<String> characterList, int length) {

		long seed = System.currentTimeMillis();
		Random random = new Random(seed);
		
		int listLen = characterList.size();
		
		StringBuilder  builder = new StringBuilder("");
		int len = 0;
		while(true) {
			int rand = random.nextInt(listLen);
			
			String word = characterList.get(rand);
			
			if (len + word.length() < length) {
				builder.append(word);
				len += word.length();
			}else{
				break;
			}
			
		}
		
		
		return builder.toString();
	}
	
	
	/**
	 * 从常用字中读取
	 * @return
	 */
	public List<String> getCharacterList() {
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
		
		return characterList;
	}
	
	
}
