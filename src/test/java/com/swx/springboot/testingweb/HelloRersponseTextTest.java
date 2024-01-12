package com.swx.springboot.testingweb;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class HelloRersponseTextTest {
	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate template;

	@Test
	void getHello() {
        ResponseEntity<String> response = template.getForEntity("/hello", String.class);
        assertThat(response.getBody()).isEqualTo("Greetings from Spring Boot (with Jetty)!");
	}

	@Test
	void greetingShouldReturnDefaultMessage() {
		// index.html content
		assertThat(this.template.getForObject("http://localhost:" + port + "/",
				String.class)).contains("<title>Tab Page</title>");
	}

}
