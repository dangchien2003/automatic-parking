package com.automaticparking.controllers;

import com.automaticparking.services.RunService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        try {
            return ResponseEntity.ok(runService.run());
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }

    @GetMapping("stop")
    ResponseEntity<?> stop() {
        try {
            return ResponseEntity.ok(runService.stop());
        } catch (BadRequestException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return serverError();
        }
    }
}
