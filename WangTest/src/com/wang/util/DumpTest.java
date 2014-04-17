package com.wang.util;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;


public class DumpTest {

	
	public static void main(String[] args) throws Exception{
		Map<Integer, ByteBuffer> map = new HashMap<Integer, ByteBuffer>();
		
		for(int i=0; i<100000; i++){
			ByteBuffer byteBuffer = ByteBuffer.allocate(10*1024*1024);
			map.put(i, byteBuffer);
		}
		
		Thread.sleep(1000);
	}
}
