
package com.wang.asmtest;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class AddFieldAdapter extends ClassVisitor{

	private int fAcc;
	private String fName;
	private String fDesc;
	private boolean isFieldPresent;
	
	public AddFieldAdapter(ClassVisitor cv, int fAcc, String fName, String fDesc) {
		super(Opcodes.ASM4, cv);
		
		this.fAcc = fAcc;
		this.fName = fName;
		this.fDesc = fDesc;
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (name.equals(fName)) {
			isFieldPresent = true;
		}
		
		return cv.visitField(access, name, desc, signature, value);
	}
	
	@Override
	public void visitEnd(){
		if (!isFieldPresent) {
			FieldVisitor fv = cv.visitField(fAcc, fName, fDesc, null, null);
			if (fv!=null) {
				fv.visitEnd();
			}
		}
		
		cv.visitEnd();
	}
	
	
	
	public static void main(String[] args) throws Exception{
		byte[] b1 = ReadClass.read("com.wang.asmtest.Output");
		ClassWriter cw = new ClassWriter(0);
		
		AddFieldAdapter ca = new AddFieldAdapter(cw, Opcodes.ACC_PUBLIC, "code", "I");
		
		ClassReader cr = new ClassReader("com.wang.asmtest.Output");
		cr.accept(ca, 0);
		
		byte[] b2 = cw.toByteArray();
		
		Output.write(b2, "Output6");
	}
}
