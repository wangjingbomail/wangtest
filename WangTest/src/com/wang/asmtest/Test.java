package com.wang.asmtest;

public class Test {


	private final int CONSUME_VALUE = 1;
	
	public  void test(float a){
		test3(a);
	}
	
	public void test1() {
		test(CONSUME_VALUE );
	}
	
	public void test3(long a) {
	    System.out.println(a);
	}
	
	public static void main(String[] args) {
//		test(3);

		Test test = new Test();
		test.test1();
	}
	
	
}
