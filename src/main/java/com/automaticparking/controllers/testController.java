package com.automaticparking.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping("user")
public class testController {
    @GetMapping("test")
    List<String> test1() {
        System.out.println("testApi1");
        return  List.of("iphone", "abc");
    }
}


