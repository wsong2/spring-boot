package com.swx.springboot.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloControllerTest {

	@Autowired
	private TestRestTemplate template;

	@Test
	void getHello() {
        ResponseEntity<String> response = template.getForEntity("/hello", String.class);
        assertThat(response.getBody()).isEqualTo("Greetings from Spring Boot (with Jetty)!");
	}

}
