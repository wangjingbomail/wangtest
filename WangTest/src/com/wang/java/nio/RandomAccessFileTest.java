package com.wang.java.nio;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试RandomAccessFile的基本用法以及性能
 * @author wang
 *
 */
public class RandomAccessFileTest {
	

	private static String objectFileName = "objectFile";
	private static String objectIndexFileName = "objectIndexFile";
	
	public static void main(String[] args) throws Exception{
		
//		int v = 1;
//		System.out.println((v >>> 24) & 0xFF );
//		System.out.println((v >>> 16) & 0xFF );
//		System.out.println((v >>> 8) & 0xFF );
//		System.out.println((v >>> 0) & 0xFF );
		
//		byte a = 1;
//		testWrite();
//		testFileChannel();
		

//		testUsage();
//		writeObject();
//		readObject();
		
//		performaceTest();
//		readPerformanceTest();
//		readPerformanceForFileChannelTest();
		writePerformaceTest2();
		readPerformanceForFileChannelTest();
//		performaceTestUseFileChannel();
	}
	
	public static void testUsage() throws Exception {
		
		RandomAccessFile randomAccessFile = new RandomAccessFile("randomAccessFile","rw");
		int max_num = 400;
		for(int i=0;i<max_num; i++ ){
			randomAccessFile.writeInt(i);;
		}
		randomAccessFile.close();
		
		RandomAccessFile readFile = new RandomAccessFile("randomAccessFile","r");
		for(int i=0; i<max_num; i++) {
			System.out.println( readFile.readInt() );
		}
		
		readFile.close();
	}
	
	
	/**
	 * 测试写入，发现是按照16位的高位先写入的选择来进行的
	 * @throws Exception
	 */
    public static void testWrite() throws Exception {
		
		RandomAccessFile randomAccessFile = new RandomAccessFile("testWrite","rw");
	    randomAccessFile.write(1);
	    randomAccessFile.writeByte(2);
	    randomAccessFile.write(3);
	    randomAccessFile.write(4);
	    
	    randomAccessFile.close();
	    
	    RandomAccessFile file = new RandomAccessFile("testWrite", "r");
	    
	    System.out.println(file.readByte());
	    System.out.println(file.readByte());
	    System.out.println(file.readByte());
	    System.out.println(file.readByte());
	    
		
		file.close();
		
	}
    
    
	
	public static void writeObject() throws Exception{
		
		List<Integer> posList = new ArrayList<Integer>();
		List<Integer> lenList = new ArrayList<Integer>();
		
		RandomAccessFile randomAccessFile = new RandomAccessFile(objectFileName,"rw");
		
		int start =0;
		int len = 0;
		int max_num = 100;
		for(int i=0; i<max_num; i++) {
			Person person = new Person();
			person.setAttr1(String.valueOf(i));
			person.setNum(i);
			person.setGender(true);
			person.setScore(new int[]{i,i+1,i+2} );
			byte[] byteArray = Person.toByte(person);
			
			len = byteArray.length;
			posList.add(start);
			lenList.add(len);
			start += len;			
			
			randomAccessFile.write(Person.toByte(person));
					
		}
		
		randomAccessFile.close();
		
		writeIndex(posList, lenList);
	}
	
	
	
	/**
	 * 把对象在文件中的位置写入索引文件中
	 * @param posList
	 * @param lenList
	 */
	public static void writeIndex(List<Integer> posList, List<Integer> lenList) throws Exception {
	    
		if (posList==null || posList.isEmpty() ||
			lenList==null || lenList.isEmpty() ) {
			System.out.println("传入值为空，写失败");
			return ;
		}
		
		if (posList.size()!=lenList.size()) {
			System.out.println("位置和长度List长度不匹配，写失败");
			return;
		}
		
		
		RandomAccessFile indexFile = new RandomAccessFile(objectIndexFileName,"rw");  	
	    
	    int size = posList.size();
	    for(int i=0; i<size; i++) {
//	    	System.out.println("pos:" + posList.get(i) + " len:" + lenList.get(i));
	    	indexFile.writeInt(posList.get(i));
	    	indexFile.writeInt(lenList.get(i));
	    	
	    }
	    
	    indexFile.close();
	    
	    
	}
	
	/**
	 * 从objectIndexFileName中读取索引文件，并把相应的值读取到posList和lenList中
	 * @param posList  对象起始位置  必须已经初始化
	 * @param lenList  对象长度    必须已经初始化
	 */
	public static void readIndex(List<Integer> posList, List<Integer> lenList) throws Exception {
	    if (posList == null || lenList==null ) {
	    	System.out.println("两个list都为null,读取失败");
	    }
	    
		posList.clear();
	    lenList.clear();
	    
	    RandomAccessFile file = new RandomAccessFile(objectIndexFileName, "r");

	    long currentPos = 0;
	    long fileLength = file.length();
	    while( currentPos<fileLength ) {
	    	System.out.println("currentPos:" + currentPos + "lengtH:" + fileLength);
	    	int start = file.readInt();
	    	int len = file.readInt();
	    	posList.add( start );
	    	lenList.add( len );
	    	
//	    	System.out.println(" start:" + start + " len:" + len);
	    	currentPos += 8;
	    	
	    }
	    
	    file.close();
	}
	
	public static void readObject() throws Exception {
		
		List<Integer> posList = new ArrayList<Integer>();
		List<Integer> lenList = new ArrayList<Integer>();
		
		readIndex(posList, lenList);
		
		
		
		RandomAccessFile file = new RandomAccessFile(objectFileName,"r");
		
		int size = posList.size();
		for(int i=0; i<size; i++) {
			int pos = posList.get(i);
			int len = lenList.get(i);
			System.out.println("pos:"+pos+" len:"+len);
			
			byte[] byteArray = new byte[len];
			file.read(byteArray);
			
			Person person = Person.parseFromByte(byteArray);
			
			System.out.println(person.toString());
		}
		
		file.close();
		
		
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public static void testFileChannel() throws Exception {
	    RandomAccessFile file = new RandomAccessFile("testFileChannel","rw");
	    FileChannel fileChannel = file.getChannel();
	    

	    ByteBuffer byteBuffer = ByteBuffer.allocate(100);
	    byte[] byteArray = new byte[]{1,2,3,4};
	  
	    byteBuffer.put(byteArray);
	    
	    byteBuffer.flip();
	    fileChannel.write(byteBuffer);
	    
	    fileChannel.close();
	    
	    
	    file.close();
	    
	    RandomAccessFile readFile = new RandomAccessFile("testFileChannel","r");
	    FileChannel readFileChannel = readFile.getChannel();
	    System.out.println("readFIle" + readFileChannel.position() + " " + readFileChannel.size());
	    
	    readFile.close();
	    
	}
	
	/**
	 * 性能测试,写入100MB的数据，看话费多长时间
	 */
	public static void writePerformaceTest() throws Exception{
		
		RandomAccessFile file = new RandomAccessFile("randomAccessPerformance","rw");
		
		
		byte[] array = new byte[1024];
		for(int i=0;i<1024;i++) {
		    array[i] = (byte)i; 

		}
		
		long begin = System.currentTimeMillis();
		int times=100*1024;
		
		for(int i=0; i<times; i++) {
			file.write(array);
		}
	
		
		long end = System.currentTimeMillis();
		
		System.out.println("time speed" + (end-begin));
		
		file.close();
		
	}
	
	public static void writePerformaceTest2() throws Exception{
		
		File file2 = new File("randomAccessPerformance2");
		if (file2.exists()) {
			file2.delete();
		}
		
		
		RandomAccessFile file = new RandomAccessFile("randomAccessPerformance2","rw");
		
		

		
		long begin = System.currentTimeMillis();
		int times=12*1024;
		
		for(long i=0; i<times; i++) {
			file.writeLong(i);
		}
	
		
		long end = System.currentTimeMillis();
		
		System.out.println("time speed" + (end-begin));
		
		file.close();
		
	}
	
	/**
	 * 测试读取的性能，读取100MB
	 * 第一次读取，文件不在缓存中，话了2540ms，后来再读取就只需要110-120ms
	 * @throws Exception
	 */
	public static void readPerformanceTest() throws Exception {
	    RandomAccessFile file = new RandomAccessFile("randomAccessPerformance", "r");
	    
	    long begin = System.currentTimeMillis();
	    byte[] byteArray = new byte[1024];
	    while(file.read(byteArray)!=-1) {
	    	
	    }
	    
	    long end = System.currentTimeMillis();
	    
	    System.out.println(" read time consume:" + (end-begin));
	    
	    file.close();
	    
	    
	}
	
	
	/**
	 * 测试读取的性能，读取100MB
	 * 每次都在290-300ms之间，有点诡异
	 * @throws Exception
	 */
	public static void readPerformanceForFileChannelTest() throws Exception {
	    RandomAccessFile file = new RandomAccessFile("randomAccessPerformance2", "r");
	    
	    FileChannel fileChannel = file.getChannel();
	    
	    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
	    System.out.println(" before:" + byteBuffer.position());
	    long begin = System.currentTimeMillis();
	    
	    while(   fileChannel.read(byteBuffer) != -1 ) {
	    	byteBuffer.flip();
	    	System.out.println( byteBuffer.getLong() );
	    	byteBuffer.clear();
	        ;	
	    }
//	    System.out.println(" after:" + byteBuffer.psosition());
	    
	    long end = System.currentTimeMillis();
	    
	    System.out.println(" read time consume:" + (end-begin));
	    
	    
	}
	
	/**
	 * 性能测试,写入100MB的数据，我这样写，但是只写入了16K的数据，而不是100M，是什么原因？
	 */
	public static void performaceTestUseFileChannel() throws Exception{
		
		File  targetFile = new File("randomAccessPerformanceForChannel");
		if ( targetFile.exists() ) {
			targetFile.delete();
		}
		
		RandomAccessFile file = new RandomAccessFile("randomAccessPerformanceForChannel","rw");
		
		int times=17;
		
		ByteBuffer[] byteBufferArray = new ByteBuffer[times];
		for(int index=0; index<times; index++){
		    
			ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			for(int i=0;i<4;i++) {
		        byteBuffer.put((byte)i);
		    }
			
			byteBuffer.flip();
		    byteBufferArray[index] = byteBuffer;
		}
		
		System.out.println(byteBufferArray.length);
		
	
		FileChannel fileChannel = file.getChannel();
		
		long begin = System.currentTimeMillis();
		
		
		fileChannel.write(byteBufferArray);
		
	
		fileChannel.close();
		long end = System.currentTimeMillis();
		
		System.out.println("time speed" + (end-begin));
		
		file.close();
		
	}

}
