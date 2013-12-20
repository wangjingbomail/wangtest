package com.wang.java.nio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileTest {

	
	public static void main(String[] args) throws Exception {
		writeFile();
	}
	
	
	public static void writeFile() throws Exception {
		File file = new File("a.txt");
		BufferedWriter  writer = new BufferedWriter(new FileWriter(file));
		  
		writer.write("abc");
		writer.flush();
		
		writer.close();
	}
}
