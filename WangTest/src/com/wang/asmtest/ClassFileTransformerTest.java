
package com.wang.asmtest;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class ClassFileTransformerTest {

	public static void premain(String agentArgs, Instrumentation inst) {
		
		inst.addTransformer(new ClassFileTransformer(){
			public byte[] transform(ClassLoader l , String name, Class c, 
					                ProtectionDomain d, byte[] b) throws IllegalClassFormatException{
				
				ClassReader cr  = new ClassReader(b);
				ClassWriter cw = new ClassWriter(cr, 0);
				ClassVisitor cv = new ChangeVersionAdapter(cw);
				cr.accept(cv, 0);
				System.out.println("ok");
				
				return cw.toByteArray();
			}
		});
	}
	
}

