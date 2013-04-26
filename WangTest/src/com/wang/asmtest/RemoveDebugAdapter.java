
package com.wang.asmtest;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;



/**
 * 可以移除一些debug的信息
 * @author wangjingbo
 *
 */
public class RemoveDebugAdapter extends ClassVisitor {

	public RemoveDebugAdapter(ClassVisitor cv) {
		super(Opcodes.ASM4, cv);
	}
	
	
	public void visitSource(String source, String debug){	
	}
	
	public void visitOuterClass(String owner, String name, String desc) {
		
	}
	
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		
	}
	
	
	public static void main(String[] args) throws Exception{
		byte[] b1 = ReadClass.read("com.wang.asmtest.Output");
		ClassWriter cw = new ClassWriter(0);
		
		RemoveDebugAdapter ca = new RemoveDebugAdapter(cw);
		
		ClassReader cr = new ClassReader("com.wang.asmtest.Output");
		cr.accept(ca, 0);
		
		byte[] b2 = cw.toByteArray();
		
		Output.write(b2, "Output3");
	}
}
