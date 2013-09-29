package com.wang.mina.clienttest;

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
import java.util.concurrent.atomic.AtomicReference;

import com.wang.mina.client.ClientState;
import com.wang.mina.client.ClientStateListener;





public class MinaClientTest extends Thread{

	
	private Selector selector;
	private List<SocketChannel> socketChannelList = new ArrayList<SocketChannel>();
	private int MAX_CHANNEL = 2;
	private final int BufferSize =  1024;
	
	private String host = "127.0.0.1";
	private int port = 8080 ;
	
    private final int retry = 10;
    
    private AtomicReference<ClientState> state = new AtomicReference<ClientState>(ClientState.Initialized);;
    
    private List<ClientStateListener> stateListeners;
    private boolean running;
    
   
    private String appid = "123";  //应用的id

    private int contentLength;   //收到的http包的内容的长度
    private int headerLength;    //收到的http包的头的长度
    
    private int KEEP_ALIVE_GAP = 1*1000; //心跳间隔  
    
    private enum ReceiveStatus {
    	COMPLETE, UNCOMPLETE,BAD,CLOSE
    };
    
    private ByteArrayOutputStream msgStream = new ByteArrayOutputStream(BufferSize);
   
    
    public MinaClientTest() {
        stateListeners = new ArrayList<ClientStateListener>();
    }
    
    public static void main(String[] args) {
		MinaClientTest client = new MinaClientTest();
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
        				System.out.println("select()");
        				if (selector.select() > 0) {
        				
        					Iterator<SelectionKey> it = selector.selectedKeys().iterator();
//        					System.out.println();
        					
           				    while(it.hasNext()) {
            				    
        				    	SelectionKey key = it.next();
        				        SocketChannel socketChannel = (SocketChannel)key.attachment();
        				    	it.remove();
        				    	
        				    	sendRequest(socketChannel,1);
        				    	sendRequest(socketChannel,1);
        				    	
        				    	if (key.isReadable()) {
        				    		receiveMessage(socketChannel);
        				    	}
        				    	
        				    	
        				    	
        				    }
           				    
           				    try {
           				        Thread.sleep(2000);
           				    }catch(Exception e) {
           				    	System.out.println("thread: " + e);
           				    }
//        				    while(it.hasNext()) {
//        				    
//        				    	SelectionKey key = it.next();
//        				        SocketChannel socketChannel = (SocketChannel)key.attachment();
//        				    	it.remove();
//        				    	
//        				    	if (key.isConnectable()) {
//        				    		   //连接就绪
//        				    		key.interestOps(SelectionKey.OP_WRITE );
//        				    	} else if (key.isWritable()) {
//        				    		    //可以发送数据了
//        				    		try {
//        				    		    Thread.sleep(1000);
//        				    		}catch(InterruptedException e) {
//        				    			System.out.println("interrupted exception" + e);
//        				    		}
//        				    		sendRequest(socketChannel, 1);
//
//        				    		key.interestOps(SelectionKey.OP_READ);
//        				    	} else if (key.isReadable()) {
//     				    		   //可以读数据了
//     				    	    	receiveMessage(socketChannel);
//     				    	    	
//     				    	    	key.interestOps(SelectionKey.OP_WRITE);
//     				    	    	
//     				    	    	
//     				    	    }
//        				    }
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
			
		    for(int i=0; i<MAX_CHANNEL; i++) {
		    	SocketChannel socketChannel = null;
			    socketChannel = SocketChannel.open();
			    socketChannel.configureBlocking(false);
			    socketChannel.socket().setReceiveBufferSize(BufferSize);
			    socketChannel.connect(new InetSocketAddress(host, port));
			    
			    SelectionKey selectionkey = socketChannel.register(selector, SelectionKey.OP_READ |
			    		                         SelectionKey.OP_WRITE|
			    		                         SelectionKey.OP_CONNECT);
			    selectionkey.attach(socketChannel);
			    
			    socketChannelList.add(socketChannel);
		    }
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
		
		for(int i=0; i<MAX_CHANNEL; i++) {
			SocketChannel socketChannel = socketChannelList.get(i);
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
		}
		
		setClientState(state);
	}
	
	private void sendRequest(SocketChannel socketChannel, long cursor) throws IOException{
		ByteBuffer buffer = buildRequestpacket(cursor);
		while (buffer.hasRemaining()) {
			socketChannel.write(buffer);
		}
		System.out.println("    send request" + socketChannel.toString());
		
		
	}
	
	
	private ByteBuffer buildRequestpacket(long cursor) {
		byte[] request = ("request" + "\r\n" ).getBytes();
				
		ByteBuffer buffer = ByteBuffer.allocate(request.length);
		buffer.put(request);
		buffer.flip();
		
		return buffer;
	}
   
	
	
  
    
    /**
     * 1.从socketChannel中读取数据HTTP包，写入msgStream中
     * 2.对其中的contentLength和headerLength进行各种检查
     * 3. 返回一个Status,用来判断读取的HTTP包是否正确。
     * @return
     * @throws IOException
     */
    private void receiveMessage(SocketChannel socketChannel) throws IOException {
    	ReceiveStatus status = ReceiveStatus.UNCOMPLETE;
    	ByteBuffer readBuffer = ByteBuffer.allocate(BufferSize);
    	
    	int readCount = 0;
    	while ( (readCount = socketChannel.read(readBuffer)) > 0 ) {
//    		System.out.println("ok");
    		readBuffer.flip();
    		byte[] bytes = new byte[readBuffer.limit()];
    		
    		readBuffer.get(bytes);
    		System.out.println("    receive:" + new String(bytes));
//    		msgStream.write(bytes);
//    		readBuffer.clear();
    	}
    	

    	
    	
    }
    
    
    /**
     * 对http包的头部进行解析，获取contengLength和headerLength字段
     * @param msgBytes
     */
    private void parseHeader(byte[] msgBytes) {
    	int lineOff = 0;
    	int pos = 0;
    	
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
    	    				contentLength = Integer.valueOf(tokens[1].trim());
    	    				headerLength = pos + 3;	
    	    				break;
    	    			}
    	    		}catch(Exception e) {
    	    			contentLength = -1;
    	    			break;
    	    		}
    	    	}
    	    	
    	    	pos += 2;   //每次递增2,也就是跳过"\r\n"
    	    	lineOff = pos;
    	    } else {
    	    	pos = pos +1;
    	    }
    	    	  
    	}
    }
    
    /**
     * 处理http消息体,把消息打印出来，然后把id给置上
     */
    private void processMessageBody() {
    	byte[] buf = msgStream.toByteArray();
    	
    	byte[] msgBody = new byte[contentLength];
    	if ( (buf.length - contentLength )> 0) {
    		System.arraycopy(buf, buf.length - contentLength, msgBody, 0, contentLength);
    		
    		String result = new String(msgBody);
    		
    	    System.out.println("    receive:" + result);
    	}
    }

    
    /**
     * 重置各种值，一般是连接关闭，并重连的时候，做的清理工作
     */
    private void clearMessage() {
    	msgStream.reset();
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
    	
    	for(int i=0; i<MAX_CHANNEL; i++) {
    		SocketChannel socketChannel = socketChannelList.get(i);
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
	
 
}
