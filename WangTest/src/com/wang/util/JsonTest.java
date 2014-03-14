package com.wang.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import cn.sina.api.commons.util.JsonBuilder;

import com.wang.domain.Door;
import com.wang.domain.Room;

public class JsonTest {

	public static void main(String[] args) {
	    test1();	
	}
	
	public static void test1() {
		
		Door door1 = new Door("south","red");
		Door door2 = new Door("north", "white");
		
		List<Door> doorList = new ArrayList<Door>();
		doorList.add(door1);
		doorList.add(door2);
		
		Room room = new Room("central", doorList);
		
		System.out.println( JSONObject.fromObject(room).toString() );
		
	}
	
	public static void test2() {
		JsonBuilder jb = new JsonBuilder();
		jb.append("text", "a");
//		jb.append(name, value)
		
	}
}
