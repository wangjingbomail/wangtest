package com.wang.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExcutorServiceTest {

	public static void main(String[] args) {
		test1();
	}
	
	public static void test1() {
		ExecutorService pool = Executors.newCachedThreadPool();
		
		
		for(int i=0; i<20; i++) {
			if (i!=10) {
				Task1 task1 = new Task1();
				pool.submit( task1 );
			}else{
				Task2 task2 = new Task2();
				pool.submit(task2);
			}
		    
		}
	}
	
	
	private static class Task1 implements Callable<String>{

		@Override
		public String call() throws Exception {
			System.out.println("ok");
			return "result";
		}
		
	};
	
	
	private static class Task2 implements Callable<String>{

		@Override
		public String call() throws Exception {
			
//			Thread.sleep(10);
			String[] array = new String[20];
			String a = "abc" + array[21];
			return a;
		}
		
	};
}
