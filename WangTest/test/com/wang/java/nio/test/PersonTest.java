package com.wang.java.nio.test;

import org.junit.Test;
import org.testng.Assert;

import com.wang.java.nio.Person;

public class PersonTest {

	@Test
	public void test(){
		
		Person person = new Person();
		person.setAttr1("abc");
		person.setGender(true);
		person.setNum(12);
		person.setScore(new int[]{2,3,5});
		
		byte[] byteArray = Person.toByte(person);
		
		Person person2 = Person.parseFromByte(byteArray);
		
		Assert.assertEquals(person, person2);
		
		Person person3 = new Person();
		person3.setAttr1("abc");
		person3.setGender(true);
		person3.setNum(12);
		person3.setScore(new int[]{2,3,5,6});
		
		Assert.assertNotEquals(person, person3);
		
	}
}
