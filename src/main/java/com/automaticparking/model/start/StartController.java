package com.automaticparking.model.start;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("start")
public class StartController {
    @GetMapping("hello")
    ResponseEntity<?> hello(){
        System.out.println("hello");
        return ResponseEntity.ok("hello");
    }
}
