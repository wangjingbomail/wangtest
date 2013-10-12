package com.wang.algorithm;

import java.util.List;

public interface StringFilter {
	/**
	 * 加入所有关键词
	 * @param words
	 */
	public void build(List<String> words);
	
	/**
	 * 判断字符串是否包含关键词
	 * 
	 * @param str
	 * @return  匹配 	- 匹配的关键词
	 * 			不匹配	- null
	 */
	public boolean match(String str);
	
	
	/**
	 * 匹配整个词
	 * @param str
	 * @return
	 */
	public boolean matchWholeWord(String str);
	
	/**
	 * 仅限字典数算法
	 * 返回上次匹配中操作次数
	 * @return
	 */
	public int getCmpStep();
	
	/**
	 * 仅限字典树算法
	 * 返回构建的字典树中的节点数
	 * 用户计算字典数占用的内存空间
	 * @return 节点数
	 */
	public int getNodeCount();
}