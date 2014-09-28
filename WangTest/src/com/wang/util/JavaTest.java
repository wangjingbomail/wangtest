package com.wang.util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class JavaTest {
	    //data的个数
	private static final int DATA_ITEMS_SIZE = 4; 
	    //索引数据的每一个所占的长度
    private static final int INDEX_ITEM_SIZE = 8+8+4;
    
    private static List<Long> idList;
    private static List<Long> dataList;
	
	public static void main(String[] args) throws Exception {
		
//        test2();
//		test3();
		
		List<Integer> list = new ArrayList<Integer>(10);
		for(int i=0; i<10; i++) {
			list.add(i);
		}
		
		System.out.println(list);
		
		
		
	}
	
	public static void test1() throws Exception{
		String urlStr = "http://www.b.com%23.www.a.com";
		
		java.net.URL url= new java.net.URL(urlStr);
		
		String host = url.getHost().toLowerCase();
		
		System.out.println( urlStr.contains("#") );
		
		System.out.println(host);
	}
	
	public static void test2() {
		byte[] byteArray = new byte[4*1024*1024];
		System.out.println("ok");
	}
	
	public static void test3() throws Exception {
		
		InetAddress address = InetAddress.getLocalHost();
		
		String ip=address.getHostAddress().toString();//获得本机IP
		
		System.out.println(ip);
		
	}
	
	/**
	 * 
	 * @param idList
	 * @param dataList
	 * @return
	 */
	public static byte[]  buildData(List<Long> idList, List<byte[]> dataList) {
//		ByteBuffer byteBuffer = ByteBuffer.allocate(24)
		
		return null;
		
		
	}
	
	/**
	 * 
	 * @param idList
	 * @param dataList
	 * @return
	 */
	public static int calCapacity(List<Long> idList, List<byte[]> dataList) {
		
		int total = 0;
		
		int len = idList.size();
		
		total += DATA_ITEMS_SIZE;  //
		
		total += (INDEX_ITEM_SIZE)*len;
		
		for(byte[] byteArray:dataList) {
			total += byteArray.length;
		}
		
		return total;
		
		
	}
	
	public static void prepareData(int dataNums){
		idList = new ArrayList<Long>();
		dataList = new ArrayList<Long>();
		
//		for(int i=0;)
	}
	
	public static void calCapacityTest() {
		List<Long> idList = new ArrayList<Long>();
		idList.add(23l);
		idList.add(2345l);
		
	}
	
	
	

}
