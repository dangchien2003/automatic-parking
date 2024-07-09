package com.automaticparking.model.run;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import response.ResponseApi;

@Controller
@RequestMapping("load")
public class RunController extends ResponseApi {
    private RunService runService;

    @Autowired
    public RunController(RunService runService) {
        this.runService = runService;
    }

    @GetMapping("run")
    ResponseEntity<?> run() {
        return runService.run();
    }

    @GetMapping("stop")
    ResponseEntity<?> stop() {
        return runService.stop();
    }
}
