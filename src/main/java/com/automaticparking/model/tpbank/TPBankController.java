package com.automaticparking.model.tpbank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tpbank")
public class TPBankController {
    private TPBankService tpBankService;

    @Autowired
    public TPBankController(TPBankService tpBankService) {
        this.tpBankService = tpBankService;
    }

    @GetMapping("run")
    ResponseEntity<?> autoApprove(@RequestParam(name = "author", required = true) String author) {
        return tpBankService.autoTpbank(author);
    }
}
