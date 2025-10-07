package com.donate4life.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // This method handles requests to the root URL (e.g., http://localhost:8080/)
    @GetMapping("/")
    public String showHomePage() {
        // It returns the string "index", which tells Spring Boot to find and render
        // the 'index.html' file from your 'src/main/resources/templates' folder.
        return "index";
    }
}