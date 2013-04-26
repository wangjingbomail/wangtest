
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
		cv.visit(version, access, name, signature, superName, interfaces);
	}

	
	public static void main(String[] args) throws Exception{
		test2();

	}
	
	public static void test1() throws Exception {
		ClassReader cr = new ClassReader("com.wang.asmtest.Output");
		ClassWriter cw = new ClassWriter(cr, 0);
		ChangeVersionAdapter adapter = new ChangeVersionAdapter(cw);
		cr.accept(adapter, 0);
		byte[] b = cw.toByteArray();
		
		Output.write(b, "Output");		
	}
	
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