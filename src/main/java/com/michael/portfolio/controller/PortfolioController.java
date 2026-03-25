package com.michael.portfolio.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PortfolioController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World";
    }
}
