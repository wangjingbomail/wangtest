package com.wang.ASMTest;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public class ClassNodeTest {

	public static void main(String[] args) throws Exception{
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader("Test");
		
		ClassVisitor
		//classNode.accept(classReader);
		
		
	}
}
