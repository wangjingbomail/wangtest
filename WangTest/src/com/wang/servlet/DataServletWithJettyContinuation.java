package com.wang.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.mortbay.jetty.nio.SelectChannelConnector.RetryContinuation;
import org.mortbay.util.ajax.ContinuationSupport;

public class DataServletWithJettyContinuation  extends HttpServlet{

	public void service(HttpServletRequest request, HttpServletResponse response) {
		
		
		System.out.println(request.getSession().getId());
		RetryContinuation continuation = (RetryContinuation) ContinuationSupport
				.getContinuation(request, null);
		
		continuation.suspend(200);
		
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 1);
			String str = jsonObject.toString();
			
		    response.getOutputStream().write((str+"\r\n").getBytes());
		    response.getOutputStream().write(("\r\n").getBytes());
		    response.getOutputStream().close();
		}catch(Exception e) {
			System.out.println("exception:"  + e);
		}
	}
	
}
