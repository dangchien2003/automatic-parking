package com.automaticparking.model.run;

import com.automaticparking.types.ResponseSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import response.ResponseApi;

@Controller
@RequestMapping("run")
public class RunController extends ResponseApi {
    private RunService runService;

    @Autowired
    public RunController(RunService runService) {
        this.runService = runService;
    }

    @GetMapping("")
    ResponseEntity<?> run() {
        try {
            runService.run();
            ResponseSuccess<String> responseSuccess = new ResponseSuccess<>();
            responseSuccess.data = "ok";
            return ResponseEntity.ok().body(responseSuccess);
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }
}
