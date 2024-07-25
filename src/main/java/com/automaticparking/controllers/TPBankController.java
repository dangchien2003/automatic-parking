package com.automaticparking.controllers;

import com.automaticparking.services.TPBankService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tpbank")
@AllArgsConstructor
public class TPBankController {
    private TPBankService tpBankService;

    @GetMapping("run")
    ResponseEntity<?> autoApprove(@RequestParam(name = "author", required = true) String author) {
        return tpBankService.autoTpbank(author);
    }
}
