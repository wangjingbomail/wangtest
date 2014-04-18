package com.wang.util;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.xerces.impl.dv.util.Base64;





public class BasicAuth {
	
	public static void main(String[] args) throws Exception{
		test();

	}
	
	public static void test() throws Exception{
		
		HttpClient httpClient = new DefaultHttpClient();
		
		HttpGet httpGet = new HttpGet("http://");                              
	    
		String username= "abc";
		String password = "def";
		String basicAuthStr = getBasicAuth(username, password);
		System.out.println(basicAuthStr);
	    httpGet.addHeader("Authorization", basicAuthStr);
	    
	    
	    HttpResponse httpResponse = httpClient.execute(httpGet);
	    

	    HttpEntity httpEntity = httpResponse.getEntity();
	    
	    if (httpEntity !=null ) {
	    	System.out.println(EntityUtils.toString(httpEntity));
	    }
	    
	}

	private static String getBasicAuth(String username, String password) {
		String str = username + ":" + password;
		return "Basic " + Base64.encode(str.getBytes());
		
	}
	

}
