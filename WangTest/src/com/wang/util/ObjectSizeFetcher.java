package com.wang.util;

import java.lang.instrument.Instrumentation;


/**
 * 得到对象所占用大小 使用premain方法启动
 * @author wang
 *
 */
public class ObjectSizeFetcher {
	private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }

}
