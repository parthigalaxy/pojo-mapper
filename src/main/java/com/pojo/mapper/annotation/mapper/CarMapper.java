package com.pojo.mapper.annotation.mapper;

import com.pojo.mapper.annotation.model.destination.MyCarModel;
import com.pojo.mapper.annotation.model.source.CarModel;
import com.pojo.mapper.annotation.types.Mapper;

public interface CarMapper {
	
	@Mapper
	MyCarModel mapCarModelToMyCarModel(CarModel carModel);

}
