package com.sagar.kagepass.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping()
    public String aliveString() {
        return "I am alive";
    }
    
}
