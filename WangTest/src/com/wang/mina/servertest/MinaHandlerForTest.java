package com.wang.mina.servertest;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

public class MinaHandlerForTest extends IoHandlerAdapter {

    public void messageReceived(IoSession session, Object message)
              throws Exception {
    	  
    	StringBuilder builder = new StringBuilder("");
		String a = "abcdefghij";
		for (int i=0; i<100; i++) {
			builder.append(a);
		}
		
		String result = builder.toString();
		
			
	    for(int i=0; i<200; i++) {
	    	session.write(result);
	    }
			    
		session.close();
    }
    
    @Override
    public void sessionOpened(IoSession session) {
        System.out.println(" session opened ");
    }
}
