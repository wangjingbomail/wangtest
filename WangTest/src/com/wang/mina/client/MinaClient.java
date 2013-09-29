package com.wang.mina.client;

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

import com.alibaba.fastjson.JSONObject;





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

    private int contentLength;   //收到的http包的内容的长度
    private int headerLength;    //收到的http包的头的长度
    
    private int KEEP_ALIVE_GAP = 1*1000; //心跳间隔  
    
    private enum ReceiveStatus {
    	COMPLETE, UNCOMPLETE,BAD,CLOSE
    };
    
    private ByteArrayOutputStream msgStream = new ByteArrayOutputStream(BufferSize);
   
    
    public MinaClient() {
        stateListeners = new ArrayList<ClientStateListener>();
    }
    
    public static void main(String[] args) {
		MinaClient client = new MinaClient();
		client.start();
	
		System.out.println("client started, target port:" + client.port);
	}
    
//    @Override
//    public void run() {
//        running = true;
//        if (init()) {
//        	while (running) {
//        		detectConnected();
//        		if (getClientState() == ClientState.Connected) {
//        			try {
//        				if (selector.select() > 0) {
//        					Iterator<SelectionKey> it = selector.selectedKeys().iterator();
//        					
//        				    while(it.hasNext()) {
//        				    	SelectionKey key = it.next();
//        				    	it.remove();
//        				    	
//        				    	if (key.isConnectable()) {
//        				    		   //连接就绪
//        				    		key.interestOps(SelectionKey.OP_WRITE);
//        				    	} else if (key.isWritable()) {
//        				    		    //可以发送数据了
//        				    		sendRequest(id.get());
//        				    		key.interestOps(SelectionKey.OP_READ);
//        				    	} else if (key.isReadable()) {
//        				    		   //可以读数据了
//        				    		ReceiveStatus status = receiveMessage();
//        				    		
//        				    		if (status == ReceiveStatus.COMPLETE) {
//        				    		    processMessageBody();
//        				    		    key.interestOps(SelectionKey.OP_WRITE);
//        				    		}else if (status == ReceiveStatus.CLOSE) {
//        				    			clearMessage();
//        				    			reconnect();
//        				    		}else{
//        				    			clearMessage();
//        				    		}
//        				    		
//        				    	}
//        				    }
//        				}
//        			}catch(IOException e) {
//        				System.out.println("could not communicate with server");
//        				clearMessage();
//        				reconnect();
//        			}
//        		}
//        		
//        		if (getClientState() == ClientState.Disconnected) {
//        			clearMessage();
//        			reconnect();
//        		}
//        	}
//        	
//        	release();
//        }
//    }
    
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

        				    		key.interestOps(SelectionKey.OP_READ);
        				    	} else if (key.isReadable()) {
     				    		   //可以读数据了
     				    	    	ReceiveStatus status = receiveMessage();
     				    	    	
     				    	    	if (status == ReceiveStatus.COMPLETE) {
     				    	    	    processMessageBody();
     				    	    	    key.interestOps(SelectionKey.OP_WRITE);
     				    	    	}else if (status == ReceiveStatus.CLOSE) {
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
    
/*
    public void run1() {

            } catch (IOException e) {
              ApiLogger.warn("Could not communicate with weipush server.", e);
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
    }*/
    
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
    private ReceiveStatus receiveMessage() throws IOException {
    	ReceiveStatus status = ReceiveStatus.UNCOMPLETE;
    	ByteBuffer readBuffer = ByteBuffer.allocate(BufferSize);
    	
    	int readCount = 0;
    	while ( (readCount = socketChannel.read(readBuffer)) > 0 ) {
    		readBuffer.flip();
    		byte[] bytes = new byte[readBuffer.limit()];
    		readBuffer.get(bytes);
    		msgStream.write(bytes);
    		readBuffer.clear();
    	}
    	
    	if (readCount == -1) {
    		status = ReceiveStatus.CLOSE;
    	}else{
    		byte[] msgBytes = msgStream.toByteArray();
    		
    		if (contentLength == 0 ){
    			parseHeader(msgBytes);  
    		}
    		
    		if (contentLength == -1) {
    			status = ReceiveStatus.BAD;
    		} else if ( contentLength == 0 ) {
    			if (msgBytes.length >= 256  ) {
    			    status  = ReceiveStatus.BAD;
    			}else {
    				status = ReceiveStatus.UNCOMPLETE;
    			}
    		} else {
    		    int expectedMsgLength = contentLength + headerLength +1;
    		    if (expectedMsgLength > msgBytes.length) {
    		    	status = ReceiveStatus.UNCOMPLETE;
    		    }else if (expectedMsgLength == msgBytes.length ) {
    		    	status = ReceiveStatus.COMPLETE;
    		    }else{
    		    	status = ReceiveStatus.BAD;
    		    }
    		}
    	}
    	
    	return status;
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
    		
    	    System.out.println("result:" + result);
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
	
 
}
