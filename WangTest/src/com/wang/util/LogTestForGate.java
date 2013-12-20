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
public class LogTestForGate {

	
	private static BufferedWriter writer;
	private static BufferedWriter writerOfStat;
	private static Map<String, List<LogEntry>> sqlMap = new HashMap<String, List<LogEntry>>();
	
	
	public static void main(String[] args) throws Exception {
		
		List<String> fileList = new ArrayList<String>();
		fileList.add("/home/wang/文档/虚拟支付/gate/28Digest.txt");
		fileList.add("/home/wang/文档/虚拟支付/gate/29Digest.txt");
		fileList.add("/home/wang/文档/虚拟支付/gate/38Digest.txt");
		fileList.add("/home/wang/文档/虚拟支付/gate/39Digest.txt");
		getLogMs(fileList);
		
		outputMap();
		
	}
	
	
	private static void getLogMs(List<String> fileList) throws Exception {
		
		openFileForWrite();
		
		
		
		String str = null;


		Pattern pattern = Pattern.compile(".*dao.*,[2-9]\\dms.*|.*dao.*,\\d{3,}?ms.*");
		
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
		       File file = new File("resultforgate.csv");
	           if (file.exists()==false) {
	 	          file.createNewFile();
	           }
	           
	           writer = new BufferedWriter(new FileWriter(file));
	           
	           File file2 = new File("resultStatforgate.csv");
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
    		
    		if (str==null ) {
    			return new LogEntry();
    		} 
    		
    		int length = str.length();
    		String content = str.substring(50, length-3);
    		if (content.indexOf("dao")!=0) {
    			return new LogEntry();
    		}
    		
    		
    		String[] array = content.split(",");
    		if (array.length>=4) {
    		    logEntry.setName(array[1]);
    		    

    		    String timeStr = array[3].substring(0, array[3].length()-2);
    		    int time = Integer.valueOf(timeStr);
    		    logEntry.setConsumeTime(time);
    		    
 
    		    
    		    logEntry.setSql(array[1]);
    		}
    		
    		return logEntry;
    	}
    	
    	
    }
}
