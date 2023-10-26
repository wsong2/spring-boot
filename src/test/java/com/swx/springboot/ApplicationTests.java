package com.swx.springboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.swx.springboot.Application;
import com.swx.springboot.controller.MiscItemController;

@SpringBootTest(classes=Application.class)
public class ApplicationTests {

	@Autowired
	MiscItemController miControler;
	
	@Test
	void contextLoads() {
		Assertions.assertThat(miControler).isNotNull();
	}

}
