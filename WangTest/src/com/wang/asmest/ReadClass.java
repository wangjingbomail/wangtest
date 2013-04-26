
package com.wang.asmtest;

import java.io.InputStream;
import java.nio.charset.Charset;

public class ReadClass {

	/**
	 * 给定一个类的全限定名，读取它的数组
	 * @param classFullName
	 * @return
	 */
	public static byte[] read(String classFullName) {
		Charset charSet = Charset.forName("US-ASCII");
        //InputStreamReader reader  = new InputStreamReader( FileTest.class.getResourceAsStream(classFullName) , charSet);
        Class theClass = null;
		try {
		    theClass = Class.forName(classFullName);

	        InputStream inputStream  = theClass.getResourceAsStream(theClass.getSimpleName()+ ".class") ;
	        
	        int count = inputStream.available();
	        
	        byte[] b = new byte[count];
	        inputStream.read(b);

	        return b;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return new byte[3];
        

        
	}
	
	public static void main(String[] args) {
		byte[] b = read("com.wang.asmtest.ReadClass");
	}
}
