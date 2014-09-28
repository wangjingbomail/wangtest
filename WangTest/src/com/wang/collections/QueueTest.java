package com.wang.collections;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueTest {

	
	private static Queue<String> queue = new LinkedBlockingQueue<String>();
	
	private static String str = "itaaaaaaaaaaaaaaaaaacadfsaaaaaaaaaaaaaaaasrwwwwwwwwwwwwww";
	
	private static Random random = new Random();
	
	public static void main(String[] args) {
		WriteTask writeTask = new WriteTask();
		writeTask.start();
		
		for(int i=0; i<16; i++) {
			ReadTask readTask = new ReadTask();
			readTask.start();
		}
	}
	
	
	public static class WriteTask extends Thread{
		
		@Override
		public void run() {
			while(true ) {
				try {
				    queue.add(str);
				
				    if (random.nextInt(100) == 1) {
				    	System.out.println("write running -- ");
					    Thread.sleep(10);
				    }
				}catch(Exception e) {
					
				}
			}
		}
	}
	
	
	public static class ReadTask extends Thread{
		
		@Override
		public void run(){
			while(true) {
				try {
				    queue.poll();
				
				    if (random.nextInt(10) == 1) {
				    	System.out.println("read running");
					    Thread.sleep(10);
				    }
				}catch(Exception e) {
					
				}
			}
			
		}
	}
}
