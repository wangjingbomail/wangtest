package com.wang.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




/**
 * 处理日志的代码
 * @author wang
 *
 */
public class LogTestForMysql {

	
	private static BufferedWriter writer;
	private static BufferedWriter writerOfStat;
	private static Map<String, List<LogEntry>> sqlMap = new HashMap<String, List<LogEntry>>();
	
	
	public static void main(String[] args) throws Exception {
		
		List<String> fileList = new ArrayList<String>();
		fileList.add("/home/wang/文档/虚拟支付/数据库相关/API系统超过20s的记录集合");
		getLogMs(fileList);
		
		outputMap();
		
	}
	
	
	private static void getLogMs(List<String> fileList) throws Exception {
		
		openFileForWrite();
		
		
		
		String str = null;


		Pattern pattern = Pattern.compile(".*,time=[2-9]\\d,.*|.*,time=\\d{3,}?.*");
		
		for(String fileName:fileList) {
			RandomAccessFile file = new RandomAccessFile(fileName, "r");
			
		    while( (str=file.readLine())!=null) {
		    	Matcher matcher =pattern.matcher(str);
		    	
		    	
		    	
		    	if (matcher.matches()) {
		    		writeFile(str, false);
		    		
		    		LogEntry logEntry = LogEntry.splitLog(str);
			    	List<LogEntry> list = sqlMap.get(logEntry.getName());
			    	if (list==null) {
			    		list = new ArrayList<LogEntry>();
			    		sqlMap.put(logEntry.getName(), list);
			    	}
			    	list.add(logEntry);
		    	}
		    }
		    
		    file.close();
		}
		
		writer.close();
		
		
	}
	
	
	 private static void openFileForWrite() {
		   try {
		       File file = new File("apilogresult.csv");
	           if (file.exists()==false) {
	 	          file.createNewFile();
	           }
	           
	           writer = new BufferedWriter(new FileWriter(file));
	           
	           File file2 = new File("apilogresultStat.csv");
	           if (file2.exists()==false) {
	 	          file2.createNewFile();
	           }
	           
	           writerOfStat = new BufferedWriter(new FileWriter(file2));
		   }catch(Exception e) {
			   System.out.println("exception : " + e);
		   }
	 }
	 
	   private static void writeFile(String str, boolean isStat) {  
		   try {
			   
		       if (isStat==false) {
		           writer.write(str  );
		           
		           Random random = new Random();
		           int seed =random.nextInt(40);
		           
		           if (seed==0) {
		               writer.flush();
		           }
		       }else{
                   writerOfStat.write(str );
		           
		           Random random = new Random();
		           int seed =random.nextInt(40);
		           
		           if (seed==0) {
		               writerOfStat.flush();
		           }
		    	   
		       }
		     
		       
		   }catch(Exception e) {
			   e.printStackTrace();
			   System.out.println(" file exception" + e);
		   }
	   } 
	 
	private static void outputMap() throws Exception {
		Iterator<String> iterator = sqlMap.keySet().iterator();
		
		while(iterator.hasNext()) {
		    String key = iterator.next();
		    
		    List<LogEntry> list = sqlMap.get(key);
		    
		    writeFile(key + "   " + list.get(0).getSql() +"\n", true);


		    
		    Collections.sort(list, new Comparator<LogEntry>(){
		    	public int compare(LogEntry entry1, LogEntry entry2) {
		    		
		    		if (entry1.getConsumeTime()>=entry2.getConsumeTime()) {
		    			return -1;
		    		}else{
		    			return 1;
		    		}
		    	}
		    	
		    	
		    });
		    
		    for(LogEntry logEntry:list) {
		    	writeFile("     " + logEntry.getConsumeTime(), true);
		    }
		    
		    writeFile("\n",true);
		}
		
		writerOfStat.flush();
		writerOfStat.close();
		
	}   
	   
    private static class LogEntry{
    	private String name;
    	private String time;
    	private int consumeTime;
    	private String sql;
    	
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public int getConsumeTime() {
			return consumeTime;
		}
		public void setConsumeTime(int consumeTime) {
			this.consumeTime = consumeTime;
		}
		public String getSql() {
			return sql;
		}
		public void setSql(String sql) {
			this.sql = sql;
		}
    	
		@Override
		public String toString() {
			return "name:" +  name + " consumetime:" + consumeTime + " time:" + time + " sql" + sql ; 
		}
    	public static LogEntry splitLog(String str) {
    		LogEntry logEntry = new LogEntry();
    		
    		if (str==null) {
    			return new LogEntry();
    		} 
    		
    		
    		
    		String log_msg = str.substring(53);
    		int colonPos = log_msg.indexOf(":");
    		int timePos = log_msg.indexOf(",time=");
    		int commaPos = log_msg.indexOf(",");
    		try {
    			String sql = "";
    			if (log_msg.indexOf("getAppOrder")!=-1) {
    				     //如果是getApp
    				 sql  = log_msg.substring(colonPos+1,commaPos);    
    			}else{
    				sql  = log_msg.substring(colonPos+1,timePos);
    			}
    		    logEntry.setSql(sql);
    		    logEntry.setName(sql);
    		}catch(StringIndexOutOfBoundsException e) {
    			System.out.println("str:" + str + " log_msg:" +log_msg + " colonPos:" + colonPos + "  timePos:" + timePos);
    		}
    		String timeTotalStr = log_msg.substring(timePos+1);
    		int timeEndPos = timeTotalStr.indexOf(",");
    		String timeStr = timeTotalStr.substring(5,timeEndPos);
    		logEntry.setConsumeTime(Integer.valueOf(timeStr));
    		
    		
    		
    		
    		return logEntry;
    	}
    	
    	
    }
}
