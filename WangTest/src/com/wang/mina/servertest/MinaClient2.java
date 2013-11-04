package com.wang.mina.servertest;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

public class MinaClient2 {
	   public static void main(String[] args) throws Exception{
	        
		  testSpeed();
	   
	    }
	   
	   
	   public static void testSpeed() {
		    SocketConnector connector = new SocketConnector();
		   
		   
		    SocketConnectorConfig config = new SocketConnectorConfig();
		    config.getSessionConfig().setReceiveBufferSize(50*1024*1024);

	        connector.getFilterChain().addLast("logger", new LoggingFilter());
	        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))) );
	        
	        
	        ClientHandler clientHandler = new ClientHandler();
	        ConnectFuture connectFuture = connector.connect(new InetSocketAddress("127.0.0.1", 8080), clientHandler, config);
	        
	        
	        connectFuture.join();
	        
	        connectFuture.getSession().write("hello");
	        
	        
	   }
	   
	   private static class Client extends Thread{
		   
		   
		   public void run() {
			   test();
		   }
		   
		   public void test(){
		        
			    SocketConnector connector = new SocketConnector();
			   
		        connector.getFilterChain().addLast("logger", new LoggingFilter());
		        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))) );
		        
		        ClientHandler clientHandler = new ClientHandler();
		        ConnectFuture connectFuture = connector.connect(new InetSocketAddress("127.0.0.1", 8080), clientHandler);
		        
		        connectFuture.join();
		        
		        connectFuture.getSession().write("hello");
		        

	       }
		   
	   };

	
}
