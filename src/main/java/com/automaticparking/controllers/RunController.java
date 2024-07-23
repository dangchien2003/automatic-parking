package com.automaticparking.controllers;

import com.automaticparking.services.RunService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("api//load")
@AllArgsConstructor
public class RunController  {
    private RunService runService;

    @GetMapping("run")
    ResponseEntity<?> run() {
        return runService.run();
    }

    @GetMapping("stop")
    ResponseEntity<?> stop() {
        return runService.stop();
    }
}
