package com.wang.jmock;

import java.util.ArrayList;
import java.util.List;

public class Publisher {

	List<Subscriber> list = new ArrayList<Subscriber>();
	
	public void add(Subscriber subscriber) {
		list.add(subscriber);
	}
	
	public void publish(String message){
		for(Subscriber subscriber:list) {
			String str = subscriber.receive(message);
			
			System.out.println("publish:" + str);
		}
	}
	
	public void talk(String message) {
		for(Subscriber subscriber:list) {
			List<String> replyMessageList = subscriber.reply(message);
			for(String str:replyMessageList) {
				System.out.println("talk:" + str);
			}
		}
	}
	
	public void say(String message) {
		for(Subscriber subscriber:list) {
			String str = subscriber.say(message);
			
			System.out.println(" say:" + str);
		}
	}
	
	public void test2() {
		for(Subscriber subscriber:list) {
			 subscriber.test2();
			
//			System.out.println(" say:" + str);
		}
	}
}
