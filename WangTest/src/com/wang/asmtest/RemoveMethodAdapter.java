
package com.wang.asmtest;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RemoveMethodAdapter extends ClassVisitor {

	private String mName;
	private String mDesc;
	
	public RemoveMethodAdapter(ClassVisitor cv, String mName, String mDesc){
		super(Opcodes.ASM4, cv);
		this.mName = mName;
		this.mDesc = mDesc;
		
	}
	
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions){
		
		if (name.equals(mName) && desc.equals(mDesc)) {
			   //把某个方法移除
			return null;
		}
		
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}
	
	public static void main(String[] args) throws Exception{
		byte[] b1 = ReadClass.read("com.wang.asmtest.Output");
		ClassWriter cw = new ClassWriter(0);
		
		RemoveMethodAdapter ca = new RemoveMethodAdapter(cw, "test", "()V");
		
		ClassReader cr = new ClassReader("com.wang.asmtest.Output");
		cr.accept(ca, 0);
		
		byte[] b2 = cw.toByteArray();
		
		Output.write(b2, "Output5");
	}
}
