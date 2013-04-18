
package com.wang.asmtest;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class ChangeVersionAdapter extends ClassVisitor {

	public ChangeVersionAdapter(ClassVisitor cv) {
		super(Opcodes.ASM4, cv);
	}
	
	public void visit(int version, int access, String name, String signature, 
			          String superName, String[] interfaces) {
		cv.visit(version, access, name, signature, superName, interfaces);
	}

	
	public static void main(String[] args) {
		
	}
}