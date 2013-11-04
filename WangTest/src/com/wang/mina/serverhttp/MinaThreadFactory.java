package com.wang.mina.serverhttp;

import java.util.concurrent.ThreadFactory;

public class MinaThreadFactory implements ThreadFactory {

	  private int i = 0;
	  private String threadName;

	  public MinaThreadFactory(String threadName) {
	    this.threadName = threadName;
	  }

	  public Thread newThread(Runnable r) {
	    i++;
	    return new Thread(r, threadName + "-" + i);
	  }

	}
