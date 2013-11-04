package com.wang.mina.serverhttp;

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
import com.wang.mina.http.HttpRequestMessage;
import com.wang.mina.http.HttpResponseMessage;

public class MinaHandler extends IoHandlerAdapter {
    
	public static SocketAcceptor socketAcceptor;
	public static int IDLE_TIME_SECONDS = 1;
//	//appId和已经发送过的最大的id的map
//	private static Map<String, String> appIdCursorIdMap = new ConcurrentHashMap<String, String>(); 
//    
	//app和确认线程处理对应的map
	private static Map<String, Thread> appAckThreadMap = new ConcurrentHashMap<String, Thread>();
	//app和填充数据线程对应的map
	private static Map<String, Thread> appFillDataThreadMap = new ConcurrentHashMap<String, Thread>();

	
	//app与待确认的消息队列的map
	private static Map<String, Queue<Message>> ackQueueMap = new ConcurrentHashMap<String, Queue<Message>>();
	//app与待发送的消息队列的map
	private static Map<String, Queue<Message>> sendQueueMap = new ConcurrentHashMap<String, Queue<Message>>();
	
	//存放确认过的id的map,key为appId+"_"+id,value为"1"
	private static Map<String, String> ackIdMap = new ConcurrentHashMap<String, String>();


	private static int SEND_QUEUE_MAX_SIZE = 5; 
	
	private static int BATCHCOUNT = 5;
	
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception{
        cause.printStackTrace();
        
    }
    
    
    public void messageReceived(IoSession session, Object message ) throws Exception{
    	

    	StringBuilder builder = new StringBuilder("");
		String a = "abcdefghi";
		for (int i=0; i<100; i++) {
			builder.append(a);
		}
		
		String result = builder.toString();

		for(int i=0; i<200; i++) {
		    HttpRequestMessage req = (HttpRequestMessage) message;
    	    HttpResponseMessage res = new HttpResponseMessage(); 	
    	    res.appendBody(result);
    	    session.write(res);
//    	    session.get
		}
		
    	session.close();
    	
        
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
     
    
    
    /**
     * 启动线程，两个线程，一个用来确认id,一个用来从数据源读取数据，让如queue中。
     * @param appid
     */
    private void startThread(String appid) {
    	
    	synchronized(appAckThreadMap) {
    		
    	    Thread fillDataThread = appFillDataThreadMap.get(appid);
    	    
    	    if (fillDataThread==null) {
    	    	fillDataThread = new FillDataThread(appid, 1, BATCHCOUNT);
    	    	appFillDataThreadMap.put(appid, fillDataThread);
    	    	fillDataThread.start();
    	    }
    		
    	    Thread ackThread = appAckThreadMap.get(appid);
    	    if (ackThread==null) {
    	    	ackThread = new AckThread(appid);
    	    	appAckThreadMap.put(appid, ackThread);
    	    	ackThread.start();
    	    	
    	    }
    	

    	}
    }
    
    
    public String getServerStatistics() {
        StringBuffer buffer = new StringBuffer();
        Set<SocketAddress> services = socketAcceptor.getManagedServiceAddresses();
        for (SocketAddress socket : services) {
          buffer.append(getSessionsStatistics(socket));
        }

        return buffer.toString();
    }
    
    private String getSessionsStatistics(SocketAddress serverSocket) {
        StringBuffer buffer = new StringBuffer();


        Set<IoSession> sessions = socketAcceptor.getManagedSessions(serverSocket);
        System.out.println("session number:" + sessions.size());
        for (IoSession session : sessions) {
            
            buffer.append(session.getReadMessages() + " " +session.getWrittenMessages());
         
        }
        return buffer.toString();
      }
   
    
    /**
     * 得到下一个id
     * 这个会有一个问题，比如id为1的连续第得不到确认，那么后面一段都会
     * @return
     */
    private String getNext(String appid) {
    		
        Queue<Message> sendQueue = sendQueueMap.get(appid);
        Message message = null;
        
        while(message == null) {
            message = sendQueue.poll();
            if (message!=null ) {
            	break;
            }else{
            	try {
            	    Thread.sleep(100);
            	}catch(InterruptedException e) {
            		System.out.println("getNext interruptedException:" + e);
            	}
            }
            
        }
        
        
        Queue<Message> ackQueue = ackQueueMap.get(appid);
        ackQueue.add(message);
        
        return JSONObject.toJSONString(message);

    }
    
    
    /**
     * 进行确认的线程
     * @author wang
     *
     */
    private static class AckThread extends Thread{
    	
    	private String appid;
    	private Queue<Message> sendQueue;
    	private DelayQueue<Message> ackQueue;
        
    	public AckThread(String appid) {
    		this.appid = appid;
    		ackQueue = new DelayQueue<Message>();
    		ackQueueMap.put(appid, ackQueue);
    		
    		sendQueue = sendQueueMap.get(appid);
    		
    	}
    	
    	
    	
    	/**
    	 * 对消息进行确认
    	 */
    	public void run() {
    		
    		while(true) {
    			
    			try {
    			    Message message = ackQueue.take();
    			    
    			    String appid = message.getAppid();
    			    String id = message.getId();
                    
    			    String key = appid + "_" + id;
    			    if (ackIdMap.get(key)!=null) {
    			        ackIdMap.remove(key);	
    			    }else{
    			    	ackQueue.add(message);
    			    	sendQueue.add(message);
    			    }
    			}catch(InterruptedException e) {
    				System.out.println("AckThread错误"+e.getMessage()+e);
    			}
 
    			
    		
    			
    		}
    		
    	}
    	
    }
    
    
    /**
     * 从某个数据源获取数据，填充到sendQueue的线程
     * @author wang
     *
     */
    public class FillDataThread extends Thread{
    
    	private int id;
    	private String appid;
    	private int batchcount = 100;
    	private Queue<Message> sendQueue;
    	public FillDataThread(String appid, int id, int batchcount) {
    		this.appid = appid;
    		this.id = id;
    		this.batchcount = batchcount;
    		this.sendQueue =  new ConcurrentLinkedQueue<Message>();
    		sendQueueMap.put(appid, sendQueue);
    	}
    	
    	public void run() {
    		while(true) {
    			try {
    			    if (sendQueue.size()> SEND_QUEUE_MAX_SIZE) {
    			    	Thread.sleep(100);
    			    }else{
    			    	int count = 0;
    			    	
    			    	while(count<batchcount) {
    			    		Message message = new Message(appid, String.valueOf(id),"");
    			    		id++;
    			    		sendQueue.add(message);
    			    		count++;
    			    	}
    			    }
    			}catch(InterruptedException e) {
    				System.out.println("filldatathread error" + e.getMessage() + e);
    			}
    		}
    		
    		
    	}
    }
}
