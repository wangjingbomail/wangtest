
package com.wang.asmtest;

import java.io.PrintWriter;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

public class TraceClassVisitorTest {

	public static void main(String[] args) {
		ClassWriter cw = new ClassWriter(0);  
        TraceClassVisitor tcv = new TraceClassVisitor(cw, new PrintWriter(System.out)); 
        CheckClassAdapter cv = new CheckClassAdapter(tcv);
        cv.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,  
                 "test/asm/examples/Comparable", null, "java/lang/Object",  
                 new String[] { "test/asm/examples/Mesurable" });  
        cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "LESS", "I", null, new Integer(-1)).visitEnd();  
        cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "EQUAL", "I", null, new Integer(0)).visitEnd();  
        cv.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "GREATER", "I", null, new Integer(1)).visitEnd();  
        cv.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "compareTo", "(Ljava/lang/Object;)I", null, null).visitEnd();  
        cv.visitEnd();  
	}
}
