package com.wang.mina.servertest;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import com.wang.mina.http.HttpResponseMessage;

public class MinaHandlerForTest extends IoHandlerAdapter {

    public void messageReceived(IoSession session, Object message)
              throws Exception {
    	  
    	System.out.println(" server receive:" + message);
      	
         
        session.write(" hello client " + session.toString() + "\r\n");
    }
}
