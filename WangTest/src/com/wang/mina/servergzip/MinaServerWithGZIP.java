package com.wang.mina.servergzip;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;

import com.wang.mina.httpgzip.HttpServerProtocolCodecFactory;



/**
 * 构建一个http包(包的body部分是一条一条的gzip压缩，每条消息单独压缩)发送给客户端
 * 客户端解压
 * @author wang
 *
 */
public class MinaServerWithGZIP {

    private SocketAcceptor socketAcceptor;
    private int port = 8080;   //开放端口
    
    public static void main(String[] args) throws IOException {
        
    	MinaServerWithGZIP server = new MinaServerWithGZIP();
    	server.prepareAcceptor();
    	server.startAcceptor();
    	
    	System.out.println("server started at port:" + server.port );
        
    	
    	
    }
    
    public void prepareAcceptor() {
    	ByteBuffer.setUseDirectBuffers(false);
    	ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
    	
    	socketAcceptor = buildAcceptor();
    	int eventThreads = 6;
    	
    	Executor eventExecutor = new ThreadPoolExecutor(eventThreads, eventThreads, 60, TimeUnit.SECONDS,
    			                   new LinkedBlockingQueue<Runnable>(), new MinaThreadFactory("event-filter"));
    	socketAcceptor.getDefaultConfig().setThreadModel(ThreadModel.MANUAL);  //不太明白
    	socketAcceptor.getFilterChain().addFirst("binary", new ProtocolCodecFilter(new HttpServerProtocolCodecFactory() ));
    	
    	socketAcceptor.getDefaultConfig().getFilterChain().addLast("threadPool", new ExecutorFilter(eventExecutor));
    }
    
  
    
    private  SocketAcceptor buildAcceptor() {
    	int ioThreads = 5;
    	Executor ioExecutor = new ThreadPoolExecutor(ioThreads+1, ioThreads+1, 60, TimeUnit.SECONDS,
    			    new LinkedBlockingQueue<Runnable>(), new MinaThreadFactory("IOExecutor"));
    	
    	SocketAcceptor socketAcceptor = new SocketAcceptor(ioThreads, ioExecutor);
    	
    	SocketAcceptorConfig socketAcceptorConfig = socketAcceptor.getDefaultConfig();
    	socketAcceptorConfig.setReuseAddress(true); //不太明白
        socketAcceptorConfig.setBacklog(256);    	 //不太明白
        SocketSessionConfig socketSessionConfig = socketAcceptorConfig.getSessionConfig(); 
   
        socketSessionConfig.setReceiveBufferSize(1024);  //1K
//        socketSessionConfig.set
        
        socketSessionConfig.setSendBufferSize(768*1024); //768K
        socketSessionConfig.setTcpNoDelay(true);
       
       
        return socketAcceptor;
    }
    
    
    public void startAcceptor() {
    	try{
    		MinaHandlerGZIP.socketAcceptor = socketAcceptor;
    	    socketAcceptor.bind(new InetSocketAddress(port), new MinaHandlerGZIP());	
    	}catch(Exception e) {
    		System.out.println("error in startAcceptor:"+e.getMessage());
    	}
    }
    
  
}
 
 
 
 

