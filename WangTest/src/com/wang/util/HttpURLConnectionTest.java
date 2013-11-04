package com.wang.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 创建很多个connection，连接服务器
 * @author wang
 *
 */
public class HttpURLConnectionTest {
	
	public static void main(String[] args) {
		
		int max_thread = 50;
		
		for(int i=0; i<max_thread; i++) {
		    ConnThread connThread = new ConnThread();
		    connThread.start();
		}
	}

	
	public static class ConnThread extends Thread {

		
		public void run() {
			try {
				while(true) {
			        URL url = new URL("http://localhost:8080/datapush/status?subid=10021&since_id=-1&format=json"); 
		            
			        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			        
			        connection.connect();
			        
			        BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream(), "UTF-8") );
			        String lines = reader.readLine();
			        System.out.println(lines);
			        reader.close();
			        connection.disconnect();

				}
			    
			    
			}catch(Exception e) {
				System.out.println(" exception:" + e);
			}
		}
	}
}
