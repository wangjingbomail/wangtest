package com.wang.netty;



import java.util.concurrent.TimeUnit;

import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

public class HashWheelTimerTest {

	
	public static void main(String[] args) {
		TimerTask task1 = new TimerTask() {
        	
        	@Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("timeout 5");
            }
        
        };
        
        TimerTask task2 = new TimerTask() {
        	
        	@Override
            public void run(Timeout timeout) throws Exception {
                System.out.println("timeout 10");
            }
        
        };
		
		final Timer timer = new HashedWheelTimer();
	    timer.newTimeout(task1, 5l, TimeUnit.SECONDS);
	    timer.newTimeout(task2, 10l, TimeUnit.SECONDS);    

	}

}
