package com.wang.java.nio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileTest {

	
	public static void main(String[] args) throws Exception {
//        A a = new A();
//        System.out.println(a.isA());
		
		String a = "";
		System.out.println(Integer.valueOf(a));
	}
	
	
	public static void writeFile() throws Exception {
		File file = new File("a.txt");
		BufferedWriter  writer = new BufferedWriter(new FileWriter(file));
		  
		writer.write("abc");
		writer.flush();
		
		writer.close();
	}
	
	static class A{
		private boolean a;

		public boolean isA() {
			return a;
		}

		public void setA(boolean a) {
			this.a = a;
		}
		
		
	}
}
