package com.wang.mina.domain;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;


/**
 * 需要发送的消息
 * @author wang
 *
 */
public class Message implements Delayed {
	
	private String appid;
	private String id;
	private String content;
	private long time;
	public static int TIMEOUT = 1000;  //延迟时间,毫秒
	
	public Message(String appid, String id, String content) {
		this.appid = appid;
		this.id = id;
		this.content = content;
		this.time = System.currentTimeMillis()+TIMEOUT;
	}
	
	public int compareTo(Delayed o) {
		long result =  this.getTime() -((Message) o).getTime() ;  
        if (result > 0) {  
            return 1;  
        }  
        if (result < 0) {  
            return -1;  
        }  
        return 0; 
	}
	
	public long getDelay(TimeUnit unit) {
		return this.time - System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "id:" + id;
	}
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	
	public static void main(String[] args) throws Exception {
		DelayQueue<Message> queue = new DelayQueue<Message>();
		
		int max = 20;
		for(int i=0; i<max; i++) {
			Message message = new Message("123", String.valueOf(i), "");
			queue.add(message);
			Thread.sleep(50);
		}
		
		int count =0;
		while(true) {
			if (count>=max) break;
			
			Message message = queue.take();
			System.out.println(message);
			count++;
			
		}
		
	
		
	}
	
	
	

}
