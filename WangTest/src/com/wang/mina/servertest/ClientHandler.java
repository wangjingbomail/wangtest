package com.wang.mina.servertest;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

public class ClientHandler extends IoHandlerAdapter{
    
    public ClientHandler(){
        
    }
    
    public long sendTime;
    public long closeTime;
    
    public volatile int times = 0;
    public volatile long len = 0;
    public volatile boolean start = false;
    public void messageReceived(IoSession session, Object message)  throws Exception {
//        if (message.equals("send ok")==false) {
//    	    System.out.println(" ok");
    	    times++;
//    	    System.out.println("times:" + times);
    	    String msg = (String)message;
    	    len += msg.length();
    	    System.out.println(message);
//        }
    }
    
    
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception{
        super.exceptionCaught(session, cause);
    }
    
    public void messageSent(IoSession session, Object message) throws Exception{
        super.messageSent(session, message);
        
        sendTime = System.currentTimeMillis();
       
    }
    
    public void sessionClosed(IoSession session) throws Exception{
        super.sessionClosed(session);
        closeTime = System.currentTimeMillis();
        System.out.println(" messages: " + times + " consume time: " + (closeTime - sendTime) + " len:" + len );
//        System.out.println("session close");
    }
    
    
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
    }
    
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }
    
    
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
//        System.out.println("sessionOpened method was called");
    }
    
}
