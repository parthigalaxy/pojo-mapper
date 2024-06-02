package com.pojo.mapper.annotation.process;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.pojo.mapper.annotation.process.MapperProcessor;

import io.toolisticon.cute.Cute;
import io.toolisticon.cute.CuteApi;

public class MapperProcessorTest{

	static CuteApi.BlackBoxTestSourceFilesInterface compileTestBuilder;
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@BeforeAll
	public static void init() {
	    compileTestBuilder = Cute
	      .blackBoxTest()
	      .given()
	      .processors(MapperProcessor.class);
	}
	
	@Test
	public void testMapperProcessor() {
	    compileTestBuilder
	      .andSourceFiles("com/pojo/mapper/annotation/mapper/CarMapper.java.ct")
	      .whenCompiled().thenExpectThat()
	      .compilationSucceeds()
	      .andThat().generatedSourceFile("com.pojo.mapper.annotation.mapper.CarMapperImpl").exists()
	      .executeTest();
	}

}
