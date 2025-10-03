package com.trustify.policy_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for testing CORS configuration
 */
@RestController
@RequestMapping("/api/public/cors-test")
public class CorsTestController {

    /**
     * Test endpoint to verify CORS configuration
     *
     * @return a simple message
     */
    @GetMapping
    public Map<String, String> corsTest() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "CORS is configured correctly!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }
}