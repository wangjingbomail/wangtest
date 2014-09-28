package com.wang.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 实现垃圾回收算法
 * @author jingbo7
 *
 */
public class GCTest {

	private static List<Long> rootIdList = new ArrayList<Long>();
	private static List<Long> allIdList = new ArrayList<Long>();
	private static List<String> refList = new ArrayList<String>();
	
	//根节点目录
	private static Map<Long, Node> idNodeMap = new HashMap<Long, Node>(); 
	
	 //能存活的节点的集合
	private static Set<Long> survivalSet = new HashSet<Long>();
	
	static{
		rootIdList.add(12l);rootIdList.add(15l);rootIdList.add(19l);
		
		allIdList.add(12l);allIdList.add(13l);allIdList.add(15l);
		allIdList.add(19l);allIdList.add(23l);allIdList.add(24l);
		allIdList.add(34l);allIdList.add(51l);allIdList.add(89l);
		
		refList.add("12,24");
		refList.add("13,24");
		refList.add("12,23");
		refList.add("34,51");
		refList.add("19,89");	
		
	}
	
	
	public static void main(String[] args) {
		
		buildTree();
		
		filterTree();
		
		clear();
		
		
	}
	
    /**
     * 构建树
     */
	private static void buildTree() {
		
		for(Long id:rootIdList){
			Node node = new Node(id);
			idNodeMap.put(id, node);
		}
		
		
		for(String ids:refList){
		    String[] idArray = ids.split(",");
		    
		    Long id = Long.valueOf(idArray[0]);
		    Long refId = Long.valueOf(idArray[1]);
		    
		    Node node = idNodeMap.get(id);
		    
		    Node refNode = idNodeMap.get(refId);
		    
		    if (node==null) {
		    	node = new Node(id);
		    	idNodeMap.put(id, node);
		    	
		    }
		    
		    if (refNode==null) {
		    	refNode = new Node(refId);
		    	idNodeMap.put(refId, refNode);
		    }
		    
		    node.childMap.put(refId, refNode);
		}
		
		
	}
	
	/**
	 * 树的过滤
	 */
	private static void filterTree() {
		for(Long rootId:rootIdList) {
			Node node = idNodeMap.get(rootId);
			if (node==null) {
				continue;
			}
			
			List<Long> idList = idNodeMap.get(rootId).getSelfAndChildrenId();
			for(Long id:idList) {
				survivalSet.addAll(idList);	
			}
		}
	}
	
	
	private static void clear() {
		allIdList = new ArrayList();
		allIdList.addAll(survivalSet);
		
		System.out.print(allIdList);
		
		List<String> refList2 = new ArrayList<String>();
		for(String ids:refList){
			 String[] idArray = ids.split(",");
			    
			 Long id = Long.valueOf(idArray[0]);
			 if (survivalSet.contains(id)) {
				 refList2.add(ids);
			 }
			 
		}
		
		refList = refList2;
		
		System.out.println(refList);
	}
	
	
	
	
	
	
	
	

	
	/**
	 * 树节点
	 * @author jingbo7
	 *
	 */
	public static class Node{
		private long id;
		public Map<Long, Node> childMap = new HashMap<Long, Node>();
		
		public Node(long id) {
			this.id = id;
		}
		
		public List<Long> getSelfAndChildrenId() {
		    List<Long> list = new ArrayList<Long>();
		    list.add(id);
		    
		    Iterator<Long> iterator = childMap.keySet().iterator();
			while(iterator.hasNext()) {
				Long refId = iterator.next();
				Node refNode = childMap.get(refId);
				
				list.addAll(refNode.getSelfAndChildrenId());
			}
			
			return list;
		    
		}
	}
	
}
