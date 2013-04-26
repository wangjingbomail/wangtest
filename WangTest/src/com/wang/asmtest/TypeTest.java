
package com.wang.asmtest;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public class TypeTest {

	public static void main(String[] args) {
		System.out.println(Type.getType(Output.class).getInternalName());
		
		Method[] methods = Output.class.getDeclaredMethods();
		System.out.println(Type.getMethodDescriptor(methods[0]));
	}
	
	public static void test1() {
        ClassNode classNode = new ClassNode();    
        
        classNode.version = Opcodes.V1_6;
        classNode.name = "ASMInterface";
        classNode.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_INTERFACE;
        classNode.superName = Type.getInternalName(Object.class);
        
       // classNode.interfaces.add(Type.getin)
        
        classNode.sourceFile = "ASMInterface.java";
        
        ClassWriter cw = new ClassWriter(0);
       
        classNode.accept(cw);
        
        FileOutputStream fos = new FileOutputStream(new File("ASMInterface.class"));
        fos.write(cw.toByteArray());
        fos.flush();
        fos.close(); 

	}
}
