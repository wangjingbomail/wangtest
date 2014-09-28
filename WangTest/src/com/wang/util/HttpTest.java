package com.wang.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.wang.java.nio.ByteBufferTest;

public class HttpTest {

	private static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private static int recBufSize = 2000;
	private static byte[] recBuf = new byte[recBufSize];

	
	public static void main(String[] args) throws Exception{
//		System.out.println("ok");
//		httpGet();
//		httpHead();
//		longLink();
		
		httpGzipTest();
	}
	
	
	public static void httpGzipTest() throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet("https://api.weibo.com/2/statuses/home_timeline.json?access_token=2.00JGc2QCWfb2GDc42191de0e0EpUA5");
        httpGet.setHeader("Accept-Encoding", "gzip");
		//		HttpGet httpGet = new HttpGet("https://api.weibo.com/proxy/echo");
        
		HttpResponse response = httpClient.execute(httpGet);
//        System.out.println(response.getEntity().getContentLength());

		byte[] byteArray = EntityUtils.toByteArray(response.getEntity());
		ByteBuffer byteBuffer =  ByteBuffer.allocate(600000);
		byteBuffer.put(byteArray);
		byteBuffer.flip();
		
		gunzip(byteBuffer);
//        System.out.println( EntityUtils.toString(response.getEntity()));
	}
	
    private static void gunzip(ByteBuffer buffer) {
		
		
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
	
	public static void httpGet() throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet("https://api.weibo.com/2/statuses/home_timeline.json?access_token=2.00DtpjEDBOC92Ec275a913a6MfCzgC");
//		HttpGet httpGet = new HttpGet("https://api.weibo.com/proxy/echo");
        
		HttpResponse response = httpClient.execute(httpGet);
        System.out.println(response.getEntity().getContentLength());
        System.out.println(response.getEntity().getContent().available());
//        System.out.println(response.getEntity().);
//        System.out.println(response.getEntity().getContent().);
		//		httpClient.e
	}
	
	public static void httpHead() throws Exception{
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpHead httpHead = new HttpHead("http://down.360safe.com/360/inst.exe");
		
		HttpResponse httpResponse = httpClient.execute(httpHead);
		System.out.println(httpResponse);
//		System.out.println( httpResponse.getHeaders("Content-Length"). );
		HttpParams map = httpResponse.getParams();
		System.out.println(httpResponse.getParams().getParameter("Content-Length") );
//		System.out.println(httpResponse.);
		
	}
	
	
	/**
	 * 测试长连接接收数据
	 */
	public static void longLink()  throws Exception{
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet("http://10.210.230.36:8080/datapush/status?subid=10050");
		
		HttpResponse httpResponse = httpClient.execute(httpGet);
		
		HttpEntity httpEntity = httpResponse.getEntity();
		
		
		if ( httpEntity==null ) {
			return ;
		}
		
		InputStream inputStream = httpEntity.getContent();
		
		
		
		while(true) {
            boolean isEnd = processLine(inputStream);
            
            if (isEnd) {
            	break;
            }
		}
		
		
	}
	
	
	/**
	 * 从inputStream
	 * @param inputStream
	 * @return  
	 * @throws Exception
	 */
	public static boolean processLine(InputStream inputStream) throws Exception {
		byte[] bytes = readLineBytes(inputStream);
		
		if (bytes!=null && bytes.length==1 && bytes[0]==-1) {
			return true;
		}
		
		if (bytes!=null && bytes.length>0) {
			String str = new String(bytes);
			System.out.println(str);
//			JSONObject jsonObject = JSONObject.fromObject(str);
			
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * 读取一行数据的byte数组内容，并返回
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readLineBytes(InputStream inputStream) throws Exception {
		
		while(true) {
		    int readCount = inputStream.read(recBuf, 0, recBufSize);
		    
		    if (readCount>0) {
		    	
		    	if ( readCount == recBufSize ) {
		    		outputStream.write(recBuf);
		    	}else{
		    		outputStream.write(recBuf, 0, readCount);
		    		
		    	}
		    }
		    
		    byte[] totalArray = outputStream.toByteArray();
		    
		    int totalLen = totalArray.length;
		    
		    int pos = 0;
		    
		    while ( pos < totalLen ) {
		    	if ( (pos+1) < totalLen) {
		    		if ( totalArray[pos]==13 && totalArray[ pos +1 ] ==10) {
		    			byte[] result = new byte[pos];
		    			
		    			System.arraycopy(totalArray, 0, result, 0, pos);
		    			
		    			int leftStart = pos +2;
		    			int leftLen = totalLen - leftStart;
		    			byte[] leftArray = new byte[ leftLen ];
		    			System.arraycopy(totalArray, leftStart, leftArray, 0, leftLen);
		    			outputStream.reset();
		    			outputStream.write(leftArray);
		    			
		    			return result;
		    		}
		    	}
		    	
		    	
		    	if ( (pos+3 ) < totalLen ) {
		    		if ( totalArray[pos]==13 && totalArray[pos] == 10 && totalArray[pos]==13 && totalArray[pos]==10 ) {
		    			return new byte[]{-1};
		    		}
		    	}
		    	
		    	pos++;
		    }

		
		}
	}
	
	
}
