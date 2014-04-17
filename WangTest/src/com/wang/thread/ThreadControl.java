package com.wang.thread;


/**
 * 线程控制类，用于控制某些处理比较慢的线程池的个数
 * @author jingbo7
 *
 */
public class ThreadControl {
	
	
	
	public static void main(String[] args) {
		
	}
	
	
	public void test() {
		
	}
	
	
	private class TheTask extends Thread {
		   //任务名
		private String task;
		public TheTask(String task){
			this.task = task;
		}
		
		@Override
		public void run() {
			try {
			    if ( "9".equals(task) ) {
			    		Thread.sleep(2000);
			    }else {
			    	Thread.sleep(50);
			    }
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	} 

}
