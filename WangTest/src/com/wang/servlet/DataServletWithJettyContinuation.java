package com.wang.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.nio.SelectChannelConnector.RetryContinuation;
import org.mortbay.util.ajax.ContinuationSupport;

public class DataServletWithJettyContinuation  extends HttpServlet{

	public void service(HttpServletRequest request, HttpServletResponse response) {
		
		
		RetryContinuation continuation = (RetryContinuation) ContinuationSupport
				.getContinuation(request, null);
		
		continuation.suspend(2000);
		
		try {
		    response.getOutputStream().write("with continuation".getBytes());
		    response.getOutputStream().close();
		}catch(Exception e) {
			System.out.println("exception:"  + e);
		}
	}
	
}
