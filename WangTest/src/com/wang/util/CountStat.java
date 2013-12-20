package com.wang.util;

import java.io.RandomAccessFile;

import com.alibaba.fastjson.JSONObject;


public class CountStat {

	
	public static void main(String[] args)  throws Exception{
		
	}
	
	public static void test1() throws Exception {
		
        RandomAccessFile file = new RandomAccessFile("home/wang/文档/push框架/测试数据/与boardreader测试/statuscurrentCount.txt","r");	
        
        String str ="";
        while((str=file.readLine())!=null){
            String[] strArray = str.split(" ");	
            JSONObject jsonObject = new JSONObject(strArray[3]);
            
        }
		
	}
	
	public static void test2() {
		
	}
}
