package com.wang.java.nio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Person implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3191154559027858540L;
	private String attr1;
	private int num;
	private boolean gender;
	private int[] score;

	@Override
	public String toString(){
		return "attr1:" + attr1 + " num:" + num + " gender:" + gender + " score:" + score;
	}
	
	public static byte[] toByte(Person person) {
		
		byte[] result = null;
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(person);
			
			result =  outputStream.toByteArray();
		
			
			outputStream.close();
			objectOutputStream.close();
		}catch(Exception e) {
			System.out.println(" toByte exception:" + e);
		}
		
		return result;
		
	}
	
	public static Person parseFromByte(byte[] byteArray) {
		Person person = null;
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(byteArray);
			ObjectInputStream oi = new ObjectInputStream(bi);
			
			person = (Person)oi.readObject();
			
			
		}catch(Exception e) {
			System.out.println("parse error:" + e);
		}
		
		return person;
	}
	
	
	@Override
	public boolean equals(Object object){
		
		Person person  = (Person)object;
		
		if ( (this.getAttr1()!=null && !this.getAttr1().equals(person.getAttr1())) ||
			 (person.getAttr1()!=null && !person.getAttr1().equals(this.getAttr1())) ) {
			return false;
		}
		
		if ( this.getNum()!=person.getNum()) {
			
			return false;
		}
		
		
		if (this.gender!=person.gender) {
			return false;
		}
		
		
		
		if ( (this.getScore()==null && person.getScore()!=null)  ||
		     (person.getScore()==null && this.getScore()!=null) ) {
			return false;
		}
		
		if (this.getScore()!=null && person.getScore()!=null) {
			if ( this.getScore().length != person.getScore().length ) {
				return false;
			}
			
			int length = this.getScore().length;
			for(int i=0; i<length; i++) {
				if (this.getScore()[i] != person.getScore()[i]) {
					return false;
				}
			}
		}
		
		
		return true;
	}
	
	public String getAttr1() {
		return attr1;
	}
	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public boolean isGender() {
		return gender;
	}
	public void setGender(boolean gender) {
		this.gender = gender;
	}
	public int[] getScore() {
		return score;
	}
	public void setScore(int[] score) {
		this.score = score;
	}  
	

	
	 
}
