package com.automaticparking.controllers;

import com.automaticparking.services.CBotService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/customer/bot")
@AllArgsConstructor
public class CBotController  {
    private CBotService cBotService;

    @GetMapping("i")
    ResponseEntity<?> getInfoBot(@RequestParam(required = false, name = "bot") String id) {
        return cBotService.geInfoBot(id);
    }
}
