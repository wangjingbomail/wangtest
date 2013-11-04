package com.wang.mina.servergzip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NoConnectionPendingException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.wang.java.nio.ByteBufferTest;
import com.wang.util.WangLogger;





public class MinaClient extends Thread{

	
	private Selector selector;
	private SocketChannel socketChannel;
	private final int BufferSize = 10 * 1024;
	
	private String host = "127.0.0.1";
	private int port = 8080 ;
	
    private final int retry = 10;
    
    private AtomicReference<ClientState> state = new AtomicReference<ClientState>(ClientState.Initialized);;
    
    private List<ClientStateListener> stateListeners;
    private boolean running;
    
    private AtomicLong id = new AtomicLong();
    private String appid = "123";  //应用的id

    private long contentLength;   //收到的http包的内容的长度
    private int headerLength;    //收到的http包的头的长度
    
    private int KEEP_ALIVE_GAP = 1*1000; //心跳间隔  
    
    private enum ReceiveStatus {
    	HEADERPROCESS, BODYPROCESS, COMPLETE, BAD,CLOSE
    };
    
   
    private ByteArrayOutputStream msgStream = new ByteArrayOutputStream();
	private ReceiveStatus receiveStatus;
	
	
    public MinaClient() {
        stateListeners = new ArrayList<ClientStateListener>();
    }
    
    public static void main(String[] args) {
		MinaClient client = new MinaClient();
		client.start();
	
		System.out.println("client started, target port:" + client.port);
	}
    
    
    public void run() {
        running = true;
        if (init()) {
        	while (running) {
        		detectConnected();
        		if (getClientState() == ClientState.Connected) {
        			try {
        				if (selector.select(KEEP_ALIVE_GAP) > 0) {
        					Iterator<SelectionKey> it = selector.selectedKeys().iterator();
        					
        				    while(it.hasNext()) {
        				    	SelectionKey key = it.next();
        				    	it.remove();
        				    	
        				    	if (key.isConnectable()) {
        				    		   //连接就绪
        				    		key.interestOps(SelectionKey.OP_WRITE );
        				    	} else if (key.isWritable()) {
        				    		    //可以发送数据了
        				    		try {
        				    		    Thread.sleep(1000);
        				    		}catch(InterruptedException e) {
        				    			System.out.println("interrupted exception" + e);
        				    		}
        				    		sendRequest(1);
        				    		receiveStatus = ReceiveStatus.HEADERPROCESS;

        				    		key.interestOps(SelectionKey.OP_READ);
        				    	} else if (key.isReadable()) {
        				    		WangLogger.debug(" coming!");
     				    		   //可以读数据了
     				    	    	receiveMessage();
     				    	    	
     				    	    	
     				    	    	if (receiveStatus == ReceiveStatus.BODYPROCESS ||
     				    	    		receiveStatus == ReceiveStatus.HEADERPROCESS	) {
     				    	    		;
     				    	    	}else if (receiveStatus == ReceiveStatus.COMPLETE) {
//     				    	    	    processMessageBody();
     				    	    		receiveStatus = ReceiveStatus.HEADERPROCESS;
     				    	    		WangLogger.info(" one request is done");
     				    	    	    key.interestOps(SelectionKey.OP_WRITE);
     				    	    	}else if (receiveStatus == ReceiveStatus.CLOSE) {
     				    	    		clearMessage();
     				    	    		reconnect();
     				    	    	}else{
     				    	    		clearMessage();
     				    	    	}
     				    	    	
     				    	    }
        				    }
        				}else{
        					   //发送心跳数据
        					sendRequest(153);
        				}
        			}catch(IOException e) {
        				System.out.println("could not communicate with server");
        				clearMessage();
        				reconnect();
        			}
        		}
        		
        		if (getClientState() == ClientState.Disconnected) {
        			clearMessage();
        			reconnect();
        		}
        	}
        	
        	release();
        }
    }
    

    
	private boolean init() {
		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.socket().setReceiveBufferSize(BufferSize);
			socketChannel.connect(new InetSocketAddress(host, port));
			
			socketChannel.register(selector, SelectionKey.OP_READ |
					                         SelectionKey.OP_WRITE|
					                         SelectionKey.OP_CONNECT);
			
			return true;
		}catch(NoConnectionPendingException e) {
			System.out.println("could not connect to server as socket channel is pending:" +e);
		}catch(ClosedChannelException e) {
			System.out.println("Counld not connect to server as socket channel is closed:" +e);
		}catch(IOException e) {
			System.out.println("could not connect to server cause of IOException:" + e);
		}
		
		return false;
	}
	
	
	public void detectConnected() {
		int times = 0;
		ClientState state = ClientState.Disconnected;
		
		try {
			while(!socketChannel.finishConnect() && times < retry) {
				times++;
				Thread.sleep(times * 1000);
			}
			
		
			if ( times < retry && !socketChannel.socket().isInputShutdown() &&
				 !socketChannel.socket().isOutputShutdown() && socketChannel.isConnected() &&
				 !socketChannel.socket().isClosed()) {
				state = ClientState.Connected;
			}
		}catch(IOException e) {
			System.out.println("could not connect to server cause of IOException:" + e);
		}catch(InterruptedException e) {
			System.out.println("unexpected InterrupedException while trying to connect to server:" + e);
		}
		
		setClientState(state);
	}
	
	private void sendRequest(long cursor) throws IOException{
		ByteBuffer buffer = buildRequestpacket(cursor);
		while (buffer.hasRemaining()) {
			socketChannel.write(buffer);
		}
		
		
	}
	
	
	private ByteBuffer buildRequestpacket(long cursor) {
		ByteBuffer buffer = buildPushPacket(cursor);
		
		return buffer;
	}
   
	private ByteBuffer buildPushPacket(long cursor) {
		String uri = "ackid=" + cursor + "&appid=" + appid;
		byte[] header = buildPushHeader(uri).getBytes();
		
		ByteBuffer buffer = ByteBuffer.allocate(header.length);
		buffer.put(header);
		buffer.flip();
		
		return buffer;
	}
	
    private String buildPushHeader(String uri) {
        StringBuilder sb = new StringBuilder();
        sb.append("GET /?" + uri + " HTTP/1.1\r\n");
        
        sb.append("User-Agent: mina client /1.0\r\n");
        sb.append("Host: " + host + ":" + port + "\r\n\r\n");
        
        return sb.toString();
    }
    
    /**
     * 1.从socketChannel中读取数据HTTP包，写入msgStream中
     * 2.对其中的contentLength和headerLength进行各种检查
     * 3. 返回一个Status,用来判断读取的HTTP包是否正确。
     * @return
     * @throws IOException
     */
    private void receiveMessage() throws IOException {
    	
    	
    	boolean isThereData = false;
    	ByteBuffer readBuffer = ByteBuffer.allocate(BufferSize);
    	while ( ( socketChannel.read(readBuffer)) > 0 ) {
    		readBuffer.flip();
    		byte[] bytes = new byte[readBuffer.limit()];
    		readBuffer.get(bytes);
    		msgStream.write(bytes);
    		readBuffer.clear();
    		isThereData = true;
    	}
    	
        
        if ( receiveStatus == ReceiveStatus.HEADERPROCESS ){
        	boolean headerOk = parseHeader();  
        	
        	if (headerOk) {
        		receiveStatus = ReceiveStatus.BODYPROCESS;
        	}
        
        }
        
        if (receiveStatus == ReceiveStatus.BODYPROCESS) {
        	boolean isComplete = parseBody();
        	
        	if (isComplete) {
        		receiveStatus = ReceiveStatus.COMPLETE;
        	}
        }

    }
    
    
    /**
     * 对http包的头部进行解析，获取contengLength和headerLength字段
     * @param msgBytes
     */
    private boolean parseHeader() {
    	int lineOff = 0;
    	int pos = 0;
    	
    	byte[] msgBytes = msgStream.toByteArray();
    	int length = msgBytes.length;
    	while ( pos < length ) {
    	    if ( (pos+1) < length &&  msgBytes[pos] == '\r' && msgBytes[pos+1] == '\n' ) {
    	    	if ( (pos - lineOff) >0 ) {
    	    		byte[] oneLineBytes = new byte[pos - lineOff];
    	    		System.arraycopy(msgBytes, lineOff, oneLineBytes, 0, pos - lineOff);
    	    		String line = new String(oneLineBytes);
    	    		String[] tokens = line.split(":");
    	    		
    	    		try {
    	    			if (tokens!=null && tokens.length == 2 && "Content-Length".equals(tokens[0])) {
    	    				contentLength = Long.valueOf(tokens[1].trim());
    	    				headerLength = pos + 4;	
    	    				
    	    				byte[] bodyByteArray =  new byte[length-headerLength];
    	    			    System.arraycopy(msgBytes, headerLength, bodyByteArray, 0, length-headerLength);
    	    				
    	    			    msgStream.reset();
    	    			    msgStream.write(bodyByteArray);
    	    			    return true;
    	    			}
    	    		}catch(Exception e) {
    	    			contentLength = -1;
    	    			return false;
    	    		}
    	    	}
    	    	
    	    	pos += 2;   //每次递增2,也就是跳过"\r\n"
    	    	lineOff = pos;
    	    } else {
    	    	pos = pos +1;
    	    }
    	    	  
    	}
    	
    	return false;
    }
    
    private boolean parseBody(){
    	
        byte[] msgBytes = msgStream.toByteArray();
        int length = msgBytes.length;
        
        int prePos =0;  //消息的起点
        int pos = 0;
        while( pos<length) {
          
            
               //整个http包的结束
            if ( (pos+4) < length && msgBytes[pos]==17 && msgBytes[pos+1] == '\r' && msgBytes[pos+2] == '\n' &&
            	  msgBytes[pos+3] == '\r' && msgBytes[pos+4] == '\n'	) {
            	return true;
            }
            
            if ( (pos+1) < length &&  msgBytes[pos] == '\r' && msgBytes[pos+1] == '\n' ) {
            	int len = pos - prePos;
            	byte[] gzipMessageBytes = new byte[len];
            	System.arraycopy(msgBytes, prePos, gzipMessageBytes, 0, len);
            	
            	byte[] messageBytes = this.gunzip(gzipMessageBytes);
            	
            	String str = new String(messageBytes);
            	int strLen = str.length();
            	WangLogger.info(" receiveMessage:" + new String(messageBytes).substring(strLen-50));
            	
            	prePos = pos+2;
            	
            }
            pos++;
        }
        

        byte[] leftBytes = new byte[ length-prePos ];
        System.arraycopy(msgBytes, prePos, leftBytes, 0, length-prePos);
        msgStream.reset();
        try{
            msgStream.write(leftBytes);
        }catch(IOException e) {
        	WangLogger.error(" leftBytes write into msgStream error!" + e);
        }
        
    	return false;
    }
   
   

    
    /**
     * 重置各种值，一般是连接关闭，并重连的时候，做的清理工作
     */
    private void clearMessage() {
    	receiveStatus = ReceiveStatus.HEADERPROCESS;
    	contentLength = 0;
    	headerLength = 0;
    }
    
    /**
     * 关闭原有连接，并重新连接
     */
    private void reconnect() {
    	try {
    		release();
    		sleep(3000);
    		init();
    	}catch(InterruptedException ex) {
    		System.out.println("重新连接被打断"+ex);
    	}
    }
    
   
    /**
     * 关闭selector和socketChannel
     */
    private void release() {
    	if (null != selector && selector.isOpen() ) {
    		try {
    			selector.close();
    		}catch(IOException e) {
    			selector = null;
    		}
    	}
    	
    	if (null != socketChannel && socketChannel.isOpen()) {
    		try {
    			socketChannel.close();
    		}catch(IOException e) {
    			socketChannel = null;
    		}
    	}
    }
    
 
    
	private void setClientState(ClientState newState) {
		ClientState oldState = state.getAndSet(newState);
		
		if (oldState != newState) {
			for (ClientStateListener listener : stateListeners) {
				listener.onStateChanged(oldState, newState);
			}
		}
	}
	
	private ClientState getClientState() {
	  return state.get();
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
