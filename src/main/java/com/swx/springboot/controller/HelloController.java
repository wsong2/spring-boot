package com.swx.springboot.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/hello")
	public String index() {
		return "Greetings from Spring Boot (with Jetty)!";
	}

	@GetMapping("/message")
    public String homePage(Model model) {
    	model.addAttribute("message", "Hi! This is message from Hello Controller");
        return "welcome";
    }
}
