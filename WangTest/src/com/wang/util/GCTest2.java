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
public class GCTest2 {

	private static List<Long> rootIdList = new ArrayList<Long>();
	private static List<Long> allIdList = new ArrayList<Long>();
	private static Map<Long, Long> refMap = new HashMap<Long, Long>();
	
	//根节点目录
	private static Map<Long, Node> idNodeMap = new HashMap<Long, Node>(); 
	
	 //能存活的节点的集合
	private static Set<Long> survivalSet = new HashSet<Long>();
	
	static{
		rootIdList.add(12l);rootIdList.add(15l);rootIdList.add(19l);
		
		allIdList.add(12l);allIdList.add(13l);allIdList.add(15l);
		allIdList.add(19l);allIdList.add(23l);allIdList.add(24l);
		allIdList.add(34l);allIdList.add(51l);allIdList.add(89l);
		
		refMap.put(12l,24l);
		refMap.put(13l,24l);
		refMap.put(15l,23l);
		refMap.put(34l,51l);
		refMap.put(19l,89l);	
		
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
		
		
		Iterator<Long> iterator = refMap.keySet().iterator();
		while(iterator.hasNext()){
			Long id = iterator.next();
			Long refId = refMap.get(id);
		 
		    
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
		
		
		for(Long id:allIdList) {
			if (survivalSet.contains(id)==false) {
				refMap.remove(id);
			}
		}
		
		allIdList = new ArrayList();
		allIdList.addAll(survivalSet);
		
		System.out.println(allIdList);
		
		
		System.out.println(refMap);
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
