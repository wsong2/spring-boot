package swx.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class HelloController
{
	@GetMapping("/hello")
    public String index() {
        return "hello";
    }
    
	@GetMapping("/message")
    public String homePage(Model model) {
    	model.addAttribute("message", "Hi! This is message from Hello Controller");
        return "welcome";
    }
}
