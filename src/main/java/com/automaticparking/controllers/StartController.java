package com.automaticparking.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/start")
public class StartController {
    @GetMapping("hello")
    ResponseEntity<?> hello() {
        Map<String, String> hello = new HashMap<>();
        System.out.println("hello"); 
        hello.put("start", "hello");
        return ResponseEntity.ok(hello);
    }
}
