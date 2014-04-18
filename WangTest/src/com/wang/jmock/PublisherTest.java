package com.wang.jmock;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class PublisherTest {

	
	
	
	@Test
	public void testOneSubscriberReceivesAMessage() {
		
		Mockery context = new Mockery();
		
		final Subscriber subscriber = context.mock(Subscriber.class);
		
		Publisher publisher = new Publisher();
		publisher.add(subscriber);
		
		final String message = "message";
		
		context.checking(new Expectations(){ {
			    //我们期待receive刚好被调用一次
			oneOf (subscriber).receive(message);
		}});
		

		publisher.publish(message);
		
		context.assertIsSatisfied();
		
	}
	
	
	/**
	 * 测试return，自己确定返回值
	 */
	@Test
	public void testReturn() {
		Mockery context = new Mockery();
		
		final Subscriber subscriber = context.mock(Subscriber.class);
		
		Publisher publisher = new Publisher();
		publisher.add(subscriber);
		
		final String message = "abc";
		final String message2 = "der";
		
		final List<String> strList = new ArrayList<String>();
		strList.add("a");
		strList.add("b");
		
		context.checking(new Expectations() {
			{
				allowing(subscriber).receive(message);
				will(returnValue("test"));
				
				allowing(subscriber).receive(message2);
				will(returnValue("gggg"));
				
				allowing(subscriber).reply(message);
				will(returnValue(strList));
				
				atLeast(1).of(subscriber).say(message);
				will(onConsecutiveCalls(returnValue("a1"),returnValue("a2"), returnValue("a3")));
			}
			
		});
		
		publisher.publish(message);   //这个里面,message参数不能修改
//		publisher.publish(message2);   //这个地方不支持，但是看文档应该是支持的，有点奇怪 
		publisher.talk(message);
		publisher.say(message);
		publisher.say(message);
		publisher.say(message);
		publisher.test2();
//		publisher.say(message);   //如果再多调用一次会出错
		
		context.assertIsSatisfied();
		
		
	}
	
}
