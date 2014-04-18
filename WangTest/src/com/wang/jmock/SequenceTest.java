package com.wang.jmock;

import junit.framework.Assert;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.junit.Test;

public class SequenceTest {
	
	
	
	/**
	 * 调用顺序的保证
	 */
	@Test
	public void test() {
		
		Mockery context = new Mockery();
		
		final Sequence sequence = context.sequence("abcer");
		final Turtle turtle = context.mock(Turtle.class);
		
		context.checking(new Expectations(){{
		    oneOf (turtle).forward(10);inSequence(sequence);
		    oneOf (turtle).turn(45);inSequence(sequence);
		    
		
		}});
		
//      下面这样，不符合它的顺序，所以会出错		
//		turtle.turn(45);
//		turtle.forward(10);

		
		turtle.forward(10);
		turtle.turn(45);
		
	}
	
	/**
	 * 测试State
	 */
	@Test
	public void test2() {
		
		Mockery context = new Mockery();
		final States state = context.states("pen").startsAs("stop");
		final Turtle turtle = context.mock(Turtle.class);
		
		context.checking(new Expectations(){{
			oneOf (turtle).forward(1);then(state.is("move"));
			oneOf (turtle).turn(3);then(state.is("turn"));
			oneOf (turtle).stop();then(state.is("stop"));
			
		}});
		
		turtle.forward(1);
		System.out.println(state.toString());
//		Assert.assertEquals("move", state.toString() );
		turtle.turn(3);
		System.out.println(state.toString());
		System.out.println(state.toString());
//		Assert.assertEquals("turn", state.toString());
        turtle.stop();
        System.out.println(state.toString());
        System.out.println(state.toString());
//        Assert.assertEquals("stop", state.toString());
		
	}
	
	
	
	

}
