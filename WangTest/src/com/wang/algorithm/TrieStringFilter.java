package com.wang.algorithm;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.wang.util.WangLogger;


/**
 * 本类使用字典树组织关键词，这样整个匹配的复杂度为 O(N*M) 其中 N为关键词的长度 M为带匹配字符串的长度。
 * 一般的字典数如下所示(其中#表示一个节点)
 * 
 * # -h-> # -e-> # -r-> # -s-> #
 * |       `i-> # -s-> #
 * `s-> # -h-> # -c-> # 
 * 上面的字典树包含了关键词  hers,his,shc
 * 
 * 本类中单个节点有256个节点指针，这样即保证了速度也使内存占用处于可以接受的程度。
 * Java中中文有一个char表示，而char在Java中为16个字节，因此在将一个char字符将如字典树中时需要将char分割成两个8位的byte类型，然后将两个byte按照char中的顺序放入字典树。
 * 
 * 非线程安全
 */

public class TrieStringFilter implements StringFilter{
	
	  /** 头节点 **/
	private Node head=null;
	
	/** 存放除根节点外的所有节点  key为树的路径. 刚开始树为空的时候，我们增加一个关键字'a'  
	 *  则会建立一个高度为3的树，根节点的key为""  第二层节点的值为a,但是它的key为225, ( 97+128=225 + ',')
	 *  第三层节点的key为根节点加上它的父亲节点再加上自己的值，也就是"225,0," ( 22500 + ',' + 0 + ',')
	 *  每个节点的path就是它在nodeMap中的key
	 * **/
	private Map<String, Node> nodeMap = new HashMap<String, Node>();
	
	//nodeCount用于记录字典树中的节点树，一个节点的大小为8*256B = 4KB，通过nodeCount可以大致算出字典数占用的内存。
	/**
	 * 如果用一节点表示一个汉字： char  16 位 2^16* 8=2^19=512kb  
	 * 用两个节点表示一个汉字  一个节点： 2^8*8=4kb 可以很大的节约内存
	 */
	private int nodeCount=0;
	private int step;
	private int keyword_num = 0;
	
	
	@Override
	public void build(List<String> words)
	{
		if(words==null){
			return ;
		}
		
		head = new Node(Node.USED,"");
		nodeCount=0;
		
		for(String word:words)
		{
			addWord(word);
		}
		
	}
	

	public void addWord(String word) {
		int len = word.length();
		Node node = head;
		
		for(int i=0;i<len;i++)
		{
			node = node.insertChar(word.charAt(i));
		}
		
		//每一个关键词接下来的Node的状态置为‘e',表明一个关键词，这样在匹配的时候如果碰到’e'，就表明匹配一个关键词了
		node.setStatus(Node.END);
		WangLogger.debug(" add path:" + node.path);
		keyword_num++;
	}
	
	/**
	 * 删除关键字
	 * @param word
	 */
	public void deleteWord(String word) {
		int len = word.length();
		
		Node node = head;
		
		for(int i=0; i<len; i++){
			
			byte[] data = charToByte(word.charAt(i));
			
			node = node.getChildNode(data[0]);
			
			if (node==null) {break;}
			
			node = node.getChildNode(data[1]);
			
			if (node==null) {break;}
		}
		
		if ( node!=null ) {
		
			node.setStatus( Node.USED );
			keyword_num--;
			WangLogger.debug(" delete path:" + node.path);
		}
	}
	
    /**
     * 是否match,如果在字典树中找到相应的词，则返回true,否则返回false
     */
	@Override
	public boolean match(String str) {
		
		//如果head为null 表明没有build就match了 
		if(head==null)
			return false;

		byte[] data = stringToByte(str);
		int len = str.length();
		for(int i=0;i<len;i++)
		{
			Node node = head;
			for(int j=i;j<len;j++)
			{
				//每次匹配，都是使用str的第i个字符开始的子串去匹配。
				step++;
				node = node.checkAndNull(data[j*2]);
				if(node==null)
				{
					break;
				}
				
				step++;
				node = node.checkAndNull(data[j*2+1]);
				if(node==null)
				{
					break;
				}
				
				if(node.getStatus() == Node.END)
				{
					//匹配的时候，i和j分别表示匹配的子串的开始和结束
					return true;

				}
				
			}
		}
		return false;
	}
	
	/**
	 * 测试整个字是否完全匹配
	 * @param word
	 * @return
	 */
	@Override
	public boolean matchWholeWord(String str){
		//如果head为null 表明没有build就match了 
		if(head==null) {
			return false;
		}
		

		byte[] data = stringToByte(str);
		int len = str.length();		
		
		Node node = head;
		for(int i=0;i<len;i++)
		{
			node = node.checkAndNull(data[i*2]);
			if (node==null) {
				return false;
			}
			
			node = node.checkAndNull(data[i*2+1]);
			if (node==null) {
				return false;
			}
			
		}
		
		if (node.getStatus() == Node.END) {
		    return true;
		}else{
			return false;
		}
		
	}
	
	@Override
	public int getNodeCount()
	{
		return nodeCount;
	}
	
	@Override
	public int getCmpStep()
	{
		return step;
	}

	/**
	 * 字典树节点。
	 */
	private class Node{
		private static final int MAXNODE = 256;
		private BitSet bitSet;
		
		/*
		 *  节点有两种状态
		 *  	'u' - 使用，但不是一个关键词的结尾
		 *  	'e' - 使用，且是一个关键词的结尾
		 */
		private static final char USED = 'u';
		private static final char END = 'e';
		private char status;
		  /** node的节点的路径,包括自身 **/
		private String path;    
	
		public Node(char status, String path)
		{
			this.bitSet = new BitSet(MAXNODE);
			this.status = status;
			this.path = path;
		
			nodeCount++;
			
			
		}
		
		/*
		 *  status相关操作
		 */
		public void setStatus(char status)
		{
			this.status=status;
		}
		public char getStatus()
		{
			return status;
		}

		
		/**
		 * 检测下一个字节是否有参数c
		 * @param c - 需要匹配下一个字节
		 * @return	匹配 	- 下一个节点
		 * 			不匹配 	- null
		 */
		public Node checkAndNull(byte c)
		{
			int c1 = c + 128;  //转为大于0，这样可以在bitSet中使用
			
			long bitBegin  = System.nanoTime();
			boolean isExist = bitSet.get(c1);
			long bitEnd = System.nanoTime();
			
			WangLogger.info(" bit " + (bitEnd-bitBegin));
			if (bitSet.get(c1)==false) {
				return null;
			}else{
				long hashBegin = System.nanoTime();
				WangLogger.info(" map size" + nodeMap.entrySet().size() );
				nodeMap.get( this.getChildNodeKey(c) );
				long hashEnd = System.nanoTime();
				WangLogger.info(" map " + (hashEnd -hashBegin));
				
			    return nodeMap.get( this.getChildNodeKey(c) );
			}
		}
		
		/**
		 * 检测下一个字节是否有参数c
		 * @param c - 需要匹配下一个字节
		 * @return 	匹配		- 下一个节点
		 * 			不匹配	- 增加对应节点并返回新增的节点
		 */
		private Node checkAndAdd(byte c)
		{
	
			int c1 = c + 128;  //转为非小于0
			Node node = null;
			String key = getChildNodeKey(c);
			
			if( bitSet.get(c1)==false)
			{
				node = new Node(Node.USED,  key);
				bitSet.set(c1);
				nodeMap.put(key, node);
			}else{
				node = nodeMap.get(key);
			}
			
			return node;
		}
		
		/**
		 * 增加一个字
		 * @param c
		 * @return
		 */
		public Node insertChar(char c)
		{
			byte[] data = charToByte(c);

			Node next = checkAndAdd(data[0]);

			return next.checkAndAdd(data[1]);
		}
		/**
		 * 取得某个节点的子节点，如果存在则返回子节点，否则返回null
		 * @param c 取值范围 [-128,127]
		 * @return
		 */
		private Node getChildNode(byte c) {			
			
			int c1 = c + 128;
			
			long bitBegin = System.nanoTime();
			boolean isExist = bitSet.get(c1);
			long bitEnd = System.nanoTime();
			WangLogger.info(" bit consume:" + (bitEnd - bitBegin));
			
			if ( isExist ) {
				
				String key = getChildNodeKey(c);
				long hashBegin = System.nanoTime();
				Node node = nodeMap.get( key);
				long hashEnd = System.nanoTime();
				WangLogger.info(" hash " + (hashEnd-hashBegin));
				return node;
			}else{
				return null;
			}
			
		}
		
		/**
		 * 得到节点的某个字节点的key,key在nodeMap中使用
		 * @param c 取值范围为[-128,127]
		 * @return
		 */
		private String getChildNodeKey(byte c) {
			long stringBegin = System.nanoTime();
			int c1 = c + 128;
			String key = this.path + c1 + ",";
			long stringEnd = System.nanoTime();
			
			WangLogger.info(" string " + (stringEnd - stringBegin));
			return key;
		} 
	}

	/**
	 * 将一个char拆分成两个byte
	 * 0000000011111111
	 * @param c
	 * @return
	 */
	private static byte[] charToByte(char c)
	{
		byte[] data = new byte[2];
		
		data[0] = (byte)(c&0x00ff);
		data[1] = (byte)((c&0xff00)>>8);
		
		return data;
	}
	
	/**
	 * 将String转化成byte，String自带的toString会对String进行转码。
	 * @param str
	 * @return
	 */
	private byte[] stringToByte(String str)
	{
		int len = str.length();
		byte[] data = new byte[len*2];
		for(int i=0;i<len;i++)
		{
			data[2*i] = (byte)(str.charAt(i)&0x00ff);
			data[2*i+1] = (byte)((str.charAt(i)&0xff00)>>8);
		}
		return data;
	}
	
	
	
	public int getKeyword_num() {
		return keyword_num;
	}


	public void setKeyword_num(int keyword_num) {
		this.keyword_num = keyword_num;
	}


	public static void main(String[] args) {
		
		String keywordsStr = "test11,test120,test141,test147,test149,test150,test151,test152,test153,test154,test155,test156,test157,test158,test159,test160,test161,test162,test163,test164,test165,test166,test167,test168,test169,test170,test171,test172,test173,test174,test175,test176,test177,test178,test179,test180,test181,test182,test183,test184,test185,test186,test187,test188,test189,test190,test191,test192,test193,test194,test195,test196,test197,test198,test199,test200,test201,test202,test203,test204,test205,test206,test207,test208,test209,test210,test211,test212,test213,test214,test215,test216,test217,test218,test219,test220,test221,test222,test223,test224,test225,test227,test228,test229,test230,test231,test232,test233,test234,test235,test236,test237,test238,test239,test241,test242,test243,test244,test245,test246,test247,test248,test249,test250,test251,test252,test253,test254,test255,test256,test257,test258,test259,test260,test261,test262,test263,test264,test265,test266,test267,test268,test269,test270,test271,test272,test273,test274,test275,test276,test277,test278,test279,test280,test3,test31,test314,test34,test343,test345,test346,test347,test348,test349,test35,test350,test351,test352,test353,test354,test355,test36,test368,test369,test37,test370,test371,test372,test373,test374,test375,test378,test379,test38,test381,test382,test383,test384,test39,test390,test391,test392,test393,test394,test395,test40,test405,test406,test41,test416,test418,test42,test421,test424,test425,test427,test451,test46,test467,test468,test469,test47,test475,test483,test484,test53,test75,test78,test90";
		StringTokenizer tokenizer = new StringTokenizer(keywordsStr, ",");
		
		List<String> list = new ArrayList<String>();
		while(tokenizer.hasMoreTokens()) {
		    list.add( tokenizer.nextToken());
		}
		
		TrieStringFilter filter = new TrieStringFilter();
		filter.build(list);
		
		TrieStringFilter2 filter2  = new TrieStringFilter2();
		filter2.build(list);
		for(int i=0; i<100; i++) {
		    WangLogger.info("--------------------------------------------------");
		    long begin = System.nanoTime();
		    boolean result = filter.match("一个笑温暖了自己，也test11柔软了世界~");
		    long end = System.nanoTime();
		    
		    long begin1 = System.nanoTime();
		    List<String> list2= filter2.getMatchWords("一个笑温暖了自己，也柔软了世界~");
		    long end1 = System.nanoTime();
		    
		    
		
		    WangLogger.info( " time :" + 1.0*(end-begin)/1000 + "  " + 1.0*(end1-begin1)/1000);
		}
		
//		Node[] nodeArray = new Node[256];
//		BitSet set = new BitSet();
//		set.set(234);
//		set.set(12);
//		
//		for(int i=0; i<100;i++) {
//		    long begin = System.nanoTime();
//		    boolean exist = (nodeArray[123]==null);
//		    long end = System.nanoTime();
//
//		    
//		    long begin1 = System.nanoTime();
//		    boolean exist2 = set.get(233);
//		    long end1 = System.nanoTime();
//		    System.out.println( "array " + (end-begin) + "  set " +(end1-begin1));
//		}
		

	}
}