
package com.wang.asmtest;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ChangeVersionAdapter extends ClassVisitor {

	public ChangeVersionAdapter(ClassVisitor cv) {
		super(Opcodes.ASM4, cv);
	}
	
	public void visit(int version, int access, String name, String signature, 
			          String superName, String[] interfaces) {
		cv.visit(Opcodes.V1_5, access, name, signature, superName, interfaces);
	}

	
	public static void main(String[] args) throws Exception{
//		test2();
		test1();

	}
	
	
	/**
	 * 这个方法相比test2，速度要快两倍，因为ClassReader组件检测到ClassVisitor是来自ClassWriter,就意味着这个方法不会被转换
	 * 也意味着：ClassReader组件不会生成相应的事件，从而节省效率
	 * @throws Exception
	 */
	public static void test1() throws Exception {
		byte[] b1 = ReadClass.read("com.wang.asmtest.Output");
		
		ClassReader cr = new ClassReader(b1);
		ClassWriter cw = new ClassWriter(cr, 0);
		ChangeVersionAdapter adapter = new ChangeVersionAdapter(cw);
		cr.accept(adapter, 0);
		byte[] b = cw.toByteArray();
		
		Output.write(b, "Output");		
	}
	
	
	/**
	 * 这个模式是:reader->adapter->writer
	 * 这个方法速度比较慢，虽然只修改了version,但是整个b1都被处理了一遍，而大部分section是不需要处理的，也就是浪费了效率
	 * @throws Exception
	 */
	public static void test2() throws Exception {
		
		
		byte[] b1 = ReadClass.read("com.wang.asmtest.Output");
		ClassWriter cw = new ClassWriter(0);
		
		ChangeVersionAdapter ca = new ChangeVersionAdapter(cw);
		
		ClassReader cr = new ClassReader("com.wang.asmtest.Output");
		cr.accept(ca, 0);
		
		byte[] b2 = cw.toByteArray();
		
		Output.write(b2, "Output2");

		
	}
}