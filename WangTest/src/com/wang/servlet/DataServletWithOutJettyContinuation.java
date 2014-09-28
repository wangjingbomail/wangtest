package com.wang.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.mortbay.jetty.nio.SelectChannelConnector.RetryContinuation;
import org.mortbay.util.ajax.ContinuationSupport;

public class DataServletWithOutJettyContinuation extends HttpServlet{

	public void service(HttpServletRequest request, HttpServletResponse response) {
		
		
		RetryContinuation continuation = (RetryContinuation) ContinuationSupport
				.getContinuation(request, null);
		
		
		
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", 1);
			String str = jsonObject.toString();
			
			Thread.sleep(200);
		    response.getOutputStream().write((str+"\r\n").getBytes());
		    response.getOutputStream().close();
		}catch(Exception e) {
			System.out.println("exception:"  + e);
		}
	}	

}
