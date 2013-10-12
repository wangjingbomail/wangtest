package com.wang.algorithm;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 *  则会建立一个高度为3的树，根节点的key为""  第二层节点的值为a,但是它的key为22500 ( 97+128=225 + '00')
	 *  第三层节点的key为根节点加上它的父亲节点再加上自己的值，也就是22500000 ( 22500 + 0 + 00)
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
		WangLogger.info(" add node path:" + node.path);
		node.setStatus(Node.END);
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
			//System.out.println( " node.path " + node.path );
			WangLogger.info(" node.path:" + node.path);
			node.setStatus( Node.USED );
		}else{
			WangLogger.info("not delete");
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
		step=0;
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
			if (bitSet.get(c1)==false) {
				return null;
			}else{
			    return nodeMap.get(path  + c1 + "00");
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
			
			if (bitSet.get(c1) ) {
				return nodeMap.get( getChildNodeKey(c) );
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
			int c1 = c + 128;
			return this.path + c1 + "00";
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
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("ab");
		list.add("a");
		list.add("abc");
		
		TrieStringFilter filter = new TrieStringFilter();
		filter.build(list);
		
		System.out.println( filter.match("a") );
		filter.deleteWord("a");
		System.out.println(filter.match("a"));
		
		
		
	}
}