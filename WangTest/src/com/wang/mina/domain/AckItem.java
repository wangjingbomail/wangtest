package com.wang.mina.domain;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;


/**
 * 这个类暂时没有用了
 * @author wang
 *
 */
public class AckItem implements Delayed {

	
	private String  id;
	private String appid;
	private long time;
	
	public int compareTo(Delayed o) {
		long result = ((AckItem) o).getTime() - this.getTime();  
        if (result > 0) {  
            return 1;  
        }  
        if (result < 0) {  
            return -1;  
        }  
        return 0; 
	}
	
	public long getDelay(TimeUnit unit) {
		return unit.convert(time-System.nanoTime(), TimeUnit.NANOSECONDS);
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

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	
	
	
}
