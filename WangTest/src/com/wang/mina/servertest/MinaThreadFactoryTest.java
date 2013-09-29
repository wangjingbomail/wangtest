package com.wang.mina.servertest;

import java.util.concurrent.ThreadFactory;

public class MinaThreadFactoryTest implements ThreadFactory {

	  private int i = 0;
	  private String threadName;

	  public MinaThreadFactoryTest(String threadName) {
	    this.threadName = threadName;
	  }

	  public Thread newThread(Runnable r) {
	    i++;
	    return new Thread(r, threadName + "-" + i);
	  }

	}
