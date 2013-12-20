package com.wang.util;

public class MyClass<X> {

	private X x;
	public <T> MyClass(T t) {
		x = (X)t;
	}
	

	public X getX() {
		return x;
	}

	public void setX(X x) {
		this.x = x;
	}



	public static void main(String[] args) {
		MyClass<Integer> myClassObject = new MyClass<Integer>(1l);
		
		System.out.println(myClassObject.getX());
	}
}
