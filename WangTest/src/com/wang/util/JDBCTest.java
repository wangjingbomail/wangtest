package com.wang.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class JDBCTest {

	public static void main(String[] args) {
		test2();
	}
	
	public static void test1() {
		
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://127.0.0.1:3306/test";
	     	
			
			Connection conn = null;
			try {
	            Class.forName(driver);
	    
	             // 连续数据库
	            conn = DriverManager.getConnection(url, "root", "root");
			
			}catch(ClassNotFoundException  e) {
				e.printStackTrace();
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
			
			try {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(" select * from student ");
				while(rs.next()) {
					int id = rs.getInt("id");
					String name = rs.getString("name");
					
					System.out.println(" id:" + id + " name:" + name);
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}
				
	}
	
	
	public static void test2() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://m4505i.hebe.grid.sina.com.cn:4505/pay_data";
     	
		
		Connection conn = null;
		try {
            Class.forName(driver);
    
             // 连续数据库
            conn = DriverManager.getConnection(url, "pay_data", "f3u4w8n7b3h");
		
		}catch(ClassNotFoundException  e) {
			e.printStackTrace();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
		try {
			
			String sql = " select * from pay_data.app_order where 1=1  and notify_time>= 1385222400000 and notify_time<=1385308800000 order by notify_time desc  limit 0,100000 ";
			PreparedStatement ps = conn.prepareStatement(sql.toString());
			//ps.setLong(3, -1);
			ResultSet rs = ps.executeQuery();
//			Statement stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery();
			
			int count =0;
			Random random = new Random();
			while(rs.next()) {
				System.out.println(rs.getString("order_id"));
				rs.getString("order_id");
				rs.getLong("appkey");
				rs.getInt("amount");
				rs.getString("description");
				System.out.println( rs.getLong("uid") );
				rs.getString("token");
				rs.getString("xft_order");
				rs.getString("remarks");
				rs.getLong("notify_time");
				rs.getLong("create_time");
				rs.getLong("end_time");
				rs.getInt("shared_amount");
				rs.getInt("check_status");
				rs.getInt("money_type");
				rs.getInt("business");
				rs.getString("version");
				rs.getInt("status");
				rs.getLong("refund_time");
				rs.getLong("order_to_uid");
				rs.getInt("user_source");
				rs.getString("return_url");
				
				count++;
				int seed = random.nextInt(100);
				if (seed==0) {
					System.out.println(" count :" + count);
				}
			}
		}catch(SQLException e) {
			System.out.println(" exception:" + e);
//			e.printStackTrace();
		}
	}
}
