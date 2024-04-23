package com.swx.springboot.mockmvc;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.swx.springboot.service.ProdctService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Disabled("Disabled until finding a way of generation csrfToken for test!")
class HelloControllerTestMvc {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProdctService service;

	@Test
	public void getHello() throws Exception {		
		when(service.greet()).thenReturn("Greetings from Spring Boot (with Jetty)!");
		mvc.perform(MockMvcRequestBuilders.get("/hello"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Greetings from Spring Boot (with Jetty)!")));

	}

}
