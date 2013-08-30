
package com.wang.asmtest;

import java.io.File;
import java.io.FileOutputStream;

public class Output {

	public int b;
	public void test(){
		System.out.println("say hello");
		
		String a = new String("ok");
		
	}
	
	
	public static void write(byte[] b, String name) {
		 try{
             FileOutputStream outputStream = new FileOutputStream( new File(name+".class") );
        
             outputStream.write(b);
//        
             outputStream.close();
		 }catch(Exception e) {
			 e.printStackTrace();
		 }

	}
	
	
	public static class A{
		public String a;
	}
}
