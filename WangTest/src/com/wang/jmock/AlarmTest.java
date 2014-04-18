package com.wang.jmock;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class AlarmTest {

	
	
	@Test
	public void test() {
		Mockery context = new Mockery();
		
		final Alarm alarm = context.mock(Alarm.class);
		
		Guard guard = new Guard(alarm);
		
		context.checking(new Expectations(){{
			oneOf (alarm).ring();
			
		}});
		
		guard.noticeBurglar();
		
	}
	
	private class Guard{
		
		private Alarm alarm;
		
		public Guard(Alarm alarm){
		    this.alarm = alarm;	
		}
		
		public void noticeBurglar(){
			Runnable ringAlarmTask = new Runnable() {
	            public void run() {
	                for (int i = 0; i < 10; i++) {
	                    alarm.ring();
	                }
	            }
	        };
	        
	        Thread ringAlarmThread = new Thread(ringAlarmTask);
	        ringAlarmThread.start();
		}
		
	}
}
