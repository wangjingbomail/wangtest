package com.wang.domain;

import java.util.List;

public class Room {

	private String name;
	private List<Door> doorList;
	
	public Room(String name, List<Door> doorList) {
		this.name = name;
		this.doorList = doorList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Door> getDoorList() {
		return doorList;
	}

	public void setDoorList(List<Door> doorList) {
		this.doorList = doorList;
	}
	
	
}
