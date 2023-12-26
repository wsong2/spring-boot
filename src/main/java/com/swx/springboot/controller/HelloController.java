package com.swx.springboot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.swx.springboot.model.Product;
import com.swx.springboot.service.IProductService;

@RestController
public class HelloController {
	
	@Autowired  
	private IProductService productService;  
	
	@GetMapping("/hello")
	public String index() {
		return "Greetings from Spring Boot (with Jetty)!";
	}

	@GetMapping("/message")
    public String homePage(Model model) {
    	model.addAttribute("message", "Hi! This is message from Hello Controller");
        return "welcome";
    }
	
	@GetMapping(value = "/product")  
	public List<Product> getProduct() {  
		List<Product> products = productService.findAll();  
		return products;  
	}
	
	@PostMapping(value = "/addproduct", consumes = "application/json", produces = "application/json")  
	public Product createProduct(@RequestBody Product product) {  
		return productService.saveProduct(product);
	}  

}

