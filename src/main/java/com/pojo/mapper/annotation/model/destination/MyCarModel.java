package com.pojo.mapper.annotation.model.destination;

import java.util.List;

public class MyCarModel {

	private String name;
	private String make;
	private String color;
	private String type;
	private String seets;
	private List<String> featurs;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMake() {
		return make;
	}
	public void setMake(String make) {
		this.make = make;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSeets() {
		return seets;
	}
	public void setSeets(String seets) {
		this.seets = seets;
	}
	public List<String> getFeaturs() {
		return featurs;
	}
	public void setFeaturs(List<String> featurs) {
		this.featurs = featurs;
	}
	
	
	
}
