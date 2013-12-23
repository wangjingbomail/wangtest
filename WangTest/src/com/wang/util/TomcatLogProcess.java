package com.wang.util;

import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * tomcat的log处理 
 * @author wang
 *
 */
public class TomcatLogProcess {
	
	private static Map<String,Long> urlAccessTimesMap = new HashMap<String, Long>();
	
	
	public static void main(String[] args) throws Exception{
		String fileName = "";
		if ( args!=null && args.length>0 ) {
			fileName = args[0];
		}
		
		process(fileName);
		
	}
	
	/**
	 * 
	 * @param fileName 使用绝对路径
	 */
	private static void process(String fileName) throws Exception{
		RandomAccessFile file = new RandomAccessFile(fileName, "r");
		String str = "";
		int count =0;
		while ( ( str = file.readLine() ) != null ) {
			
			if(str.contains(" 200 ")==false) {
				continue;
			}
			
			String urlPath = extractUrlPath(str);
			
			if (urlPath!=null) {
				addToMap(urlPath);
			}else{
				System.out.println(urlPath);
			}
			
			count++;
		}
		
		file.close();
		System.out.println(" totalcount: " + count);
		output();
	}
	
	
	/**
	 * 从日志文件中提取访问路径
	 * @param logLine
	 * @return
	 */
	private static String extractUrlPath(String logLine) {
	
		String regex = "\".*\"";
		
		Pattern pattern = Pattern.compile(regex);
		
		 Matcher matcher = pattern.matcher(logLine);
		
		String firstMatch = null ;
		if (matcher.find()) {
			firstMatch = matcher.group();
		}
		
	    if ( firstMatch!=null ) {
	        String[] array = firstMatch.split(" ");
	        
	        int index= array[1].indexOf("?");
	        
	        if (index==-1) {
	        	return array[1];
	        }else{
	        	return array[1].substring(0, index);
	        }
	    }
		
		return null;
	}
	
	private static void addToMap(String urlPath) {
		Long no = urlAccessTimesMap.get(urlPath);
		
		if (no==null ) {
			urlAccessTimesMap.put(urlPath, 1l);
		}else{
			
			urlAccessTimesMap.put(urlPath, ++no);
		}
	}
	
	private static void output() {
		Iterator<String> iterator = urlAccessTimesMap.keySet().iterator();
		
		while(iterator.hasNext()) {
			String key = iterator.next();
			System.out.println( key + "  " + urlAccessTimesMap.get(key));
		}
	}

}
