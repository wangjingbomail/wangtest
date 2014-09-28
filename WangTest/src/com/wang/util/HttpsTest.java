package com.wang.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsTest {

	public static void main(String[] args) throws Exception {
		X509TrustManager trustManager = new Java2000TrustManager();
		TrustManager mytm[] = {trustManager};
		
		SSLContext ctx = SSLContext.getInstance("SSL");
		ctx.init(null, mytm, null);
		
		SSLSocketFactory factory = ctx.getSocketFactory();
		
	
		
		
	}
	
	
	public static class Java2000TrustManager implements X509TrustManager {
		  Java2000TrustManager() {
		    // 这里可以进行证书的初始化操作
		  }
		 
		  // 检查客户端的可信任状态
		  public void checkClientTrusted(X509Certificate chain[], String authType) throws CertificateException {
		    System.out.println("检查客户端的可信任状态...");
		  }
		 
		  // 检查服务器的可信任状态
		  public void checkServerTrusted(X509Certificate chain[], String authType) throws CertificateException {
		    System.out.println("检查服务器的可信任状态");
		  }
		 
		  // 返回接受的发行商数组
		  public X509Certificate[] getAcceptedIssuers() {
		    System.out.println("获取接受的发行商数组...");
		    return null;
		  }
		}
}
