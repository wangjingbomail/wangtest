package com.wang.jmock;

import java.util.List;

public interface Subscriber {

	/**
	 * 收到消息
	 * @param message
	 * @return
	 */
	public String receive(String message);
	
	
	/**
	 * 回复消息
	 * @param message
	 * @return
	 */
	public List<String> reply(String message);
	
	/**
	 * 说话
	 * @param message
	 * @return
	 */
	public String say(String message);

}
