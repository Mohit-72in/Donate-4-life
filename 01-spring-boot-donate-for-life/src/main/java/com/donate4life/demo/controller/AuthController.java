package com.donate4life.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLoginForm() {
        // This simply returns the name of the 'login.html' template
        return "login";
    }
}