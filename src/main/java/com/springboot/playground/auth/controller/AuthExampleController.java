package com.springboot.playground.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthExampleController {

    @GetMapping("/api/public")
    public Map<String, String> getPublicData() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "This is a PUBLIC endpoint. Anyone can access this without a JWT token!");
        return response;
    }

    @GetMapping("/api/secured")
    public Map<String, String> getSecuredData() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "This is a SECURED endpoint. Your Bearer JWT token was successfully validated!");
        return response;
    }
}
