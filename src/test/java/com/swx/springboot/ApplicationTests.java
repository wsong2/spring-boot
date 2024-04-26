package com.swx.springboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.swx.springboot.controller.MiscItemController;

@SpringBootTest(classes=Application.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApplicationTests {

	@Autowired
	MiscItemController miController;
	
	@Test
	void contextLoads() {
		Assertions.assertThat(miController).isNotNull();
	}

}
