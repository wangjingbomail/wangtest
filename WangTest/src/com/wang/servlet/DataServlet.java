package com.wang.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DataServlet extends HttpServlet{

	public void service(HttpServletRequest request, HttpServletResponse response) {
		
		StringBuilder builder = new StringBuilder("");
		String a = "abcdefghi";
		for (int i=0; i<100; i++) {
			builder.append(a);
		}
		
		String result = builder.toString();
		
		try {
			
			for(int i=0; i<20000; i++) {
		        response.getOutputStream().write( result.getBytes() );
			}
			
		    response.getOutputStream().close();
		}catch(Exception e) {
			System.out.println("exception:"  + e);
		}
	}
}
