package com.wang.thread;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 测试ConcurrentLinkedQueue
 * @author jingbo7
 *
 */
public class ConcurrentLinkedQueueTest {

	private static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
	
	public static void main(String[] args) {
	
		
	}
	
	private static class ProduceTask extends Thread{
		
		@Override
		public void run() {
			while(true){
				queue.add("1");
			}
		}
	}
	
	private static class ConsumeTask extends Thread {
		
		@Override
		public void run() {
			while(true) {
				while(true) {
				    queue.poll();
				}
			}
		}
	}
}
