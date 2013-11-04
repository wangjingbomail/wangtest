package com.wang.algorithm;



import java.util.List;
/**
 * 本程序由钟华完成
 * 本类使用字典树组织关键词，这样整个匹配的复杂度为 O(N*M) 其中 N为关键词的长度 M为带匹配字符串的长度。
 * 一般的字典数如下所示(其中#表示一个节点)
 * 
 * # -h-> # -e-> # -r-> # -s-> #
 * |       `i-> # -s-> #
 * `s-> # -h-> # -c-> # 
 * 上面的字典树包含了关键词  hers,his,shc
 * 
 * 本类中单个节点有256个节点指针，这样即保证了速度也使内存占用处于可以接受的程度。
 * Java中中文由一个char表示，而char在Java中为16个字节，因此在将一个char字符将如字典树中时需要将char分割成两个8位的byte类型，然后将两个byte按照char中的顺序放入字典树。
 * 
 * @author joker
 *
 */

public class TrieStringFilterV2 {
	
	private Node head=null;
	
	//nodeCount用于记录字典树中的节点树，一个节点的大小为8*256B = 4KB，通过nodeCount可以大致算出字典数占用的内存。
	private int nodeCount=0;	
	
	
	/**
	 * 根据传入的关键词集合沟将字典数
	 * 构建完成后，可以调用getNodeCount获得字典树中节点的数目
	 * 
	 * @param words 
	 */
	public void build(List<String> words)
	{
		if(words==null)
			return ;
		
		head = new Node(Node.USED);
		nodeCount=0;
		
		for(String word:words)
		{
//			if("脚高事等".equals(word))
//			{
//				System.out.println("hello");
//			}
			addWord(word);
		}
	}
	

	/**
	 * 往字典数中加入单个关键词
	 * 
	 * @param word
	 */
	private void addWord(String word) {
		int len = word.length();
		Node node = head;
		
		for(int i=0;i<len;i++)
		{
			node = insertChar(node,word.charAt(i));
		}
		
		//每一个关键词接下来的Node的状态置为‘e',表明一个关键词，这样在匹配的时候如果碰到’e'，就表明字符串匹配了一个关键词
		node.setStatus(Node.END);
	}
	
	/**
	 * 在节点node后面插入字符c
	 * 字符c会被产分成2个byte加入到node后面，因此最多会创建两个node
	 * @param node
	 * @param c
	 * @return
	 */
	private Node insertChar(Node node,char c)
	{
		byte[] data = charToByte(c);
		
		Node next = node.checkAndAdd(data[0]);
		
		return next.checkAndAdd(data[1]);
	}

	/**
	 * 检查字符串str中是否有字典树中的关键词
	 * 	如果有 返回第一个匹配的关键词
	 * 	如果没有 返回null
	 * 
	 * @param str
	 */
	public String match(String str) {
		
		//如果head为null 表明没有build就match了 
		if(head==null)
			return null;
		byte[] data = stringToByte(str);
		int len = str.length();
		for(int i=0;i<len;i++)
		{
			Node node = head;
			for(int j=i;j<len;j++)
			{
				//每次匹配，都是使用str的第i个字符开始的子串去匹配。
				node = node.checkAndNull(data[j*2]);
				if(node==null)
				{
					break;
				}
				
				node = node.checkAndNull(data[j*2+1]);
				if(node==null)
				{
					break;
				}
				
				if(node.getStatus() == Node.END)
				{
					//匹配的时候，i和j分别表示匹配的子串的开始和结束
					return str.substring(i, j+1);
				}
			}
		}
		return null;
	}
	
	
	public int getNodeCount()
	{
		return nodeCount;
	}
	
	/**
	 * 字典树节点。
	 * @author joker
	 */
	private class Node{
		private static final int MAXNODE = 256;
		private byte[] next;
		private Node[] nodeList;
		private short size;
		
		/*
		 *  节点有两种状态
		 *  	'u' - 使用，但不是一个关键词的结尾
		 *  	'e' - 使用，且是一个关键词的结尾
		 */
		private static final char USED = 'u';
		private static final char END = 'e';
		private char status;
	
		public Node(char status)
		{
			this.status = status;
			next = new byte[MAXNODE];
			for(int i=0;i<MAXNODE;i++)
				next[i]=(byte)(0xFF);
			nodeList = new Node[0];
			size=0;
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
		public boolean checkStatus(char status)
		{
			return this.status==status;
		}
		
		/*
		 * nodelis相关操作
		 */
		private Node[] growOneNode()
		{
			Node[] newNodeList = new Node[size+1];
			for(int i=0;i<size;i++)
				newNodeList[i] = nodeList[i];
			newNodeList[size] = new Node(Node.USED);
			size++;
			nodeList = newNodeList;
			return newNodeList;
		}
		
		
		
		/**
		 * 检测下一个字节是否有参数c
		 * @param c - 需要匹配下一个字节
		 * @return	匹配 	- 下一个节点
		 * 			不匹配 	- null
		 */
		public Node checkAndNull(byte c)
		{
			int index = next[c&0xff]&0xff;
			return index<size?nodeList[index]:null;
		}
		
		/**
		 * 检测下一个字节是否有参数c
		 * @param c - 需要匹配下一个字节
		 * @return 	匹配		- 下一个节点
		 * 			不匹配	- 增加对应节点并返回新增的节点
		 */
		public Node checkAndAdd(byte c)
		{
			int index = c&0xff;
			int nextindex = next[index]&0xff;
			if(nextindex>=size)
			{
				next[index] = (byte)(size&0xff);
				nextindex = next[index]&0xff;
				growOneNode();
			}
			
			return nodeList[nextindex];
		}
	}

	/**
	 * 将一个char拆分成两个byte
	 * @param c
	 * @return
	 */
	private byte[] charToByte(char c)
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
}

