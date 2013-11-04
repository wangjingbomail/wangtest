package com.wang.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.wang.java.nio.ByteBufferTest;

public class GZIPTest {

    public static void main(String[] args) throws Exception{
	    
//    	ByteBuffer buffer = gzipTest();
    	
//    	System.out.println((byte)'\0');
    	
//    	gzipAndGunzip();
    	
    	readGzipFile();
	}
    
    
    public static void readGzipFile() throws Exception{
    	
    	
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	RandomAccessFile file = new RandomAccessFile("/home/wang/桌面/text.log","r");
    	
    	file.seek(400*1024*1024);
    	byte b = 0;
    	int count = 0;
    	try{
    	    while ( true){
    	       b = file.readByte();
    		   stream.write(b);
    		   count++;
    		   
//    		   if (count>100) {
//    			   break;
//    		   }
    	    }
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    	byte[] byteArray = stream.toByteArray();
    	
    	
//    	totalArray[pos] == 13 && totalArray[pos + 1] == 10 && 
//   			 totalArray[pos + 2]==31 && totalArray[ pos + 3]== -117
    	
    	int pos = 0;
    	ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
    	for(int i=0; i<byteArray.length-5; i++) {
    	    if ( byteArray[i] == 13  && byteArray[i+1] == 10 
    	         && byteArray[i+2]==31 && byteArray[i+3]==-117 ) {
    	        byte[] oneMessageByteArray = stream2.toByteArray();
    	        
    	        byte[] messageByteArray = gunzip(oneMessageByteArray);
    	        System.out.println(new String(messageByteArray));
    	        
    	        
    	        stream2.reset();
    	        i +=1;
    	    }else{
    	    	stream2.write(byteArray[i]);
    	    } 
    	    
    	}
    	
    	
    	
    	
    	
    }
    
    private static void gzipAndGunzip() {
    	
    	StringBuilder builder = new StringBuilder("");
    	for(int i=0; i<100; i++) {
    	    builder.append("abcdefgh中国i牛j龙:{\"" + "_" + i + "_");
    	}
    	byte[] byteArray = gzip(builder.toString().getBytes());
    	
    	byte[] output = gunzip(byteArray);
    	
    	System.out.println(new String(output));
    	
    	
    }
    
    private static void gzipTest() {
    	ByteBuffer buffer = ByteBuffer.allocate(200);

    	for(int i=0; i<100; i++) {
    		buffer.putChar('a');
    	}
    	
    	buffer.flip();
    	ByteBuffer buffer2 = gzip(buffer);
    	
    	int count =0;
    	
    	while( count < buffer2.limit()) {
    		System.out.print(" " + buffer2.get() );
    		count++;
    	}
    }
    
	private static ByteBuffer gzip(ByteBuffer byteBuffer) {  
	  
	  int beforeSize = byteBuffer.limit();
	  ByteBuffer resultBuffer = ByteBuffer.allocate(byteBuffer.limit()/2);

	  try{
	      ByteArrayOutputStream bos = new ByteArrayOutputStream();
	      GZIPOutputStream gzip = new GZIPOutputStream(bos);
	      

	      byte[] byteArray = ByteBufferTest.getByteArray(byteBuffer);

	      gzip.write( byteArray );
	      gzip.finish();
	      gzip.close();
	      
	      resultBuffer.put(bos.toByteArray());
	      resultBuffer.flip();
	      bos.close();
	  }catch(IOException e) {
		  WangLogger.error("gzip error in HttpResponseEncoder" + e);
	  }
	  
	  int afterSize = resultBuffer.limit();
	  WangLogger.info(" gzip: before size:" + beforeSize + " after size:" + afterSize);
	  return resultBuffer;
	   
	}
	
	private static byte[] gzip(byte[] inputByteArray) {
		try{
		      ByteArrayOutputStream bos = new ByteArrayOutputStream();
		      GZIPOutputStream gzip = new GZIPOutputStream(bos);

		      gzip.write( inputByteArray );
		      gzip.finish();
		      gzip.close();
		      
		      byte[] resultByteArray = bos.toByteArray();
		      bos.close();
		      return resultByteArray;
		      
		  }catch(IOException e) {
			  WangLogger.error("gzip error in HttpResponseEncoder" + e);
		  }
		
		  return new byte[1];
	}
	
	private void gunzip(ByteBuffer buffer) {
		
		
		try{
			ByteArrayInputStream bis = new ByteArrayInputStream(ByteBufferTest.getByteArray(buffer));
			GZIPInputStream stream = new GZIPInputStream(bis);
			
			byte[] byteArray = new byte[4];
			while(stream.read(byteArray, 0, 4)!=-1){
				System.out.println(new String(byteArray));
			}
		}catch(IOException e) {
			System.out.println(" exception:" + e);
		}
		
	}
	
	public static byte[] gunzip(byte[] inputByteArray) {
		try{
			ByteArrayInputStream bis = new ByteArrayInputStream(inputByteArray);
			GZIPInputStream stream = new GZIPInputStream(bis);
			
			ByteBuffer buffer = ByteBuffer.allocate(10000);
			int byteRead = 0;
			while( (byteRead=stream.read() )!=-1 ){
				buffer.put((byte)byteRead);
			}
			
			buffer.flip();
			return ByteBufferTest.getByteArray(buffer);
		}catch(IOException e) {
			System.out.println(" exception:" + e);
		}
		return new byte[1];
	}
	
	
	  
}
