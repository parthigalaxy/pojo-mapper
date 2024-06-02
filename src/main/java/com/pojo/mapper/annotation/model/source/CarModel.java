package com.pojo.mapper.annotation.model.source;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarModel {

	private String name;
	private String make;
	private String color;
	private String type;
	private String seets;
	private List<String> featurs;
	
}
