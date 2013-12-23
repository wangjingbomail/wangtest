package com.wang.util;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * 对比import org.apache.commons.lang3.builder.ToStringBuilder;来生成toString
 * 和自己来生成toString的耗时，cpu消耗，内存消耗
 * @author wang
 *
 */
public class ToStringTest2 {

	
	public static void main(String[] args) {
		
		if (args!=null && args.length>=1 && "toString".equals(args[0])) {
			cpuConsumeTest("toString");
		}else{
			cpuConsumeTest("toString2");
		}
//		Order order = new Order();
//		System.out.println(order);
////		for(int i=0; i<10; i++) {
////		    consumeTest();
////		}
		
	}
	
	
	public static void consumeTest() {
		
		long consume1 = 0;
		long consume2 = 0;
		Order order = new Order();
		for(int i=0; i<10000; i++) {
			long begin1 = System.currentTimeMillis();
			order.toString();
			long end1 = System.currentTimeMillis();
			
			consume1 += (end1-begin1);
			
			long begin2 = System.currentTimeMillis();
			order.toString2();
			long end2 = System.currentTimeMillis();
			
			consume2 += (end2-begin2);
			
			
		}
		
		System.out.println(" consume1:" + consume1 + " consume2:" + consume2 + " " + consume1*1.0/consume2);
		
	}
	
	
	public static void cpuConsumeTest (String method) {
		Order order = new Order();
//		System.out.println(order.toString2());
		if ( "toString".equals(method) ) {
			System.out.println("toString");
			while(true) {
				order.toString();
			}
		}else{
			System.out.println("toString2");
			while(true) {
				order.toString2();
			}
		}
	}
	
	private static class Order {
		private String orderId="343434343";
		private String desc = "中国节";
		private Integer appkey = 23890024 ;
		private Long amount = 12345l;
		private Long uid = 423428910002l;
		private Date createTime = new Date();
		private Date finishTime = new Date();;
		private Byte status = 2;
		
//		private ClassA classA = new ClassA("WANG", "CHINA");
		
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
		
		public String toString2() {
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(" orderId:" + orderId);
			strBuilder.append(" desc:" + desc);
			strBuilder.append(" appkey:" + appkey);
			strBuilder.append(" amount:" + amount);
			strBuilder.append(" uid:" + uid);
			strBuilder.append(" createTime:" + createTime);
			strBuilder.append(" finishTime:" + finishTime);
			strBuilder.append(" status:" + status);
			
			return strBuilder.toString();
		}
		
		public String getOrderId() {
			return orderId;
		}
		public void setOrderId(String orderId) {
			this.orderId = orderId;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public Integer getAppkey() {
			return appkey;
		}
		public void setAppkey(Integer appkey) {
			this.appkey = appkey;
		}
		public Long getAmount() {
			return amount;
		}
		public void setAmount(Long amount) {
			this.amount = amount;
		}
		public Long getUid() {
			return uid;
		}
		public void setUid(Long uid) {
			this.uid = uid;
		}
		public Date getCreateTime() {
			return createTime;
		}
		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}
		public Date getFinishTime() {
			return finishTime;
		}
		public void setFinishTime(Date finishTime) {
			this.finishTime = finishTime;
		}
		public Byte getStatus() {
			return status;
		}
		public void setStatus(Byte status) {
			this.status = status;
		}
		
		
	}
	
	public static class ClassA {
		private String name;
		private String address;
		
		public ClassA(String name, String address) {
			this.name = name;
			this.address = address;
		}
		
		
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		
		
	}
	
	

}
