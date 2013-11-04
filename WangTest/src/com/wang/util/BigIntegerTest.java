package com.wang.util;

import java.math.BigInteger;

public class BigIntegerTest {

	public static void main(String[] args) {
		BigInteger bigInteger = new BigInteger("245");

		for(int i=0; i<200; i++) {
			bigInteger = bigInteger.multiply(new BigInteger("256"));
		}
		
		System.out.println(bigInteger);
		
		
	}
}
