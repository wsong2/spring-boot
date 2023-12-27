package com.swx.springboot.testingweb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.swx.springboot.controller.MiscItemController;

@SpringBootTest
class ContestLoadTest {
	
	@Autowired
	private MiscItemController miItemController;

	@Test
	void contextLoads() {
		assertThat(miItemController).isNotNull();
	}

}
