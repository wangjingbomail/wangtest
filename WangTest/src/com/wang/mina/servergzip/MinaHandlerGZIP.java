package com.wang.mina.servergzip;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.transport.socket.nio.SocketAcceptor;

import com.alibaba.fastjson.JSONObject;
import com.wang.mina.domain.Message;
import com.wang.mina.httpgzip.HttpRequestMessage;
import com.wang.mina.httpgzip.HttpResponseMessage;


public class MinaHandlerGZIP extends IoHandlerAdapter {
    
	public static SocketAcceptor socketAcceptor;
	public static int IDLE_TIME_SECONDS = 2;


	public void exceptionCaught(IoSession session, Throwable cause) throws Exception{
        cause.printStackTrace();
        
    }
    
    
    public void messageReceived(IoSession session, Object message ) throws Exception{
    	    	

    	HttpResponseMessage res = new HttpResponseMessage(); 	
    	res.setDataType(HttpResponseMessage.DATA_TYPE_HEADER);
    	session.write(res);
    	
    	StringBuilder builder = new StringBuilder("");
    	for(int i=0; i<2000;i++) {
    		builder.append(i%10);
    	}
    	String appendStr = builder.toString();
    	
    	for(int i=0;i<5;i++) {
    		String str = appendStr + " 中国ij_-+-Time  aj$%!@#$%^&*()_+~" + "_" + i+ "_";
    		res = new HttpResponseMessage();
    		res.setDataType(HttpResponseMessage.DATA_TYPE_BODY);
    		res.appendBody(str);
    		
    		session.write(res);
    		Thread.sleep(5);
    	}
    	
    	
    	res = new HttpResponseMessage();
    	res.setDataType(HttpResponseMessage.DATA_TYPE_TAIL);
    	session.write(res);
    	session.close();
                
//        System.out.println(getServerStatistics());
        
    }
    

    /**
     * 打开一个session
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
    	System.out.println("session opened");
    	session.setIdleTime(IdleStatus.READER_IDLE, IDLE_TIME_SECONDS);
    }
    
    
    /**
     * 关闭session
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
    	System.out.println("session close");
    }
    
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        
     	if (status == IdleStatus.READER_IDLE) {
     		System.out.println("IDLE " + session.getIdleCount(status));
     		session.close();
     	}
     }
     
    
}
