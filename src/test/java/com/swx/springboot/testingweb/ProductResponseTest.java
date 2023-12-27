package com.swx.springboot.testingweb;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.swx.springboot.model.Product;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductResponseTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int randomServerPort;
	
	@Test
	void testAddProductSuccess() throws URISyntaxException {
        final String baseUrl = "http://localhost:"+randomServerPort+"/addproduct";
        URI uri = new URI(baseUrl);
        
        Product product = new Product(103L, "Laptop", "LHP29OCP", 24000.00, 1);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");      
        
        HttpEntity<Product> request = new HttpEntity<>(product, headers);
        ResponseEntity<Product> result = this.restTemplate.postForEntity(uri, request, Product.class);
        assertThat(result.getBody().getBatchNo()).isEqualTo("LHP29OCP");
	}

}
